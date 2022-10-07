package melsec.commands.multi;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.types.CommandCode;
import melsec.types.DataType;
import melsec.types.PlcKey;
import melsec.types.io.IORequestItem;
import melsec.types.io.IORequestUnit;
import melsec.types.io.IOResponse;
import melsec.types.io.IOResponseItem;
import melsec.utils.*;

import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MultiBlockBatchBaseCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  protected static final int MAX_POINTS = 960;
  /**
   *
   */
  protected static final int MAX_BLOCKS = 120;
  //endregion

  //region Class members
  /**
   *
   */
  protected HashMap<PlcKey, IORequestItem> map;
  /**
   *
   */
  protected List<IPlcObject> results;
  /**
   *
   */
  protected List<IPlcObject> bits;
  /**
   *
   */
  protected List<IPlcObject> words;
  //endregion

  //region Class properties
  /**
   *
   * @param o
   * @return
   */
  protected static int getPointsCount( IPlcObject o ){
    return switch( o.type() ){
      case Bit -> 1;
      default -> ByteConverter.getBytesCount( ( IPlcWord ) o ) / 2;
    };
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param u
   */
  public MultiBlockBatchBaseCommand( IORequestUnit u ){
    map = new HashMap<>();
    results = new ArrayList<>();
    unit = u;

    bits = UtilityHelper
      .toStream( u.items() )
      .filter( x -> x.object().type() == DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    words = UtilityHelper
      .toStream( u.items() )
      .filter( x -> x.object().type() != DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    UtilityHelper
      .toStream( u.items() )
      .forEach( x -> map.put( UtilityHelper.getKey( x.object()), x ) );
  }
  /**
   *
   * @return
   */
  @Override
  public String toString(){
    String w = words.size() > 0 ? MessageFormat.format( "{0}w", words.size() ) : "";
    String b = bits.size() > 0 ? MessageFormat.format( "{0}b", bits.size() ) : "";
    String sep = ( !w.isEmpty() && !b.isEmpty() ) ? "|" : "";

    var c = ( getCode() == CommandCode.MultiBlockBatchWrite ) ? "w" : "r";
    var shortId = id.substring( 0, 3 );

    return MessageFormat.format( "mbb{4}#{3} [{0}{1}{2}]", w, sep, b, shortId, c );
  }
  //endregion

  //region Class 'Response' methods
  /**
   *
   * @return
   */
  @Override
  public IOResponse toResponse( Throwable e ){
    return ( null == e ) ? toOkResponse() : toNgResponse( e );
  }
  /**
   *
   * @return
   */
  private IOResponse toOkResponse(){
    var output = new HashMap<PlcKey, IOResponseItem>();

    for( var o : results ){
      var key = UtilityHelper.getKey( o );

      if( !map.containsKey( key ) )
        continue;

      var req = map.get( key );
      output.put( key, req.toResponse( o ) );
    }

    var items = new ArrayList<IOResponseItem>();

    for( var item: unit.items() ){
      var key = UtilityHelper.getKey( item.object());

      if( !output.containsKey( key ) )
        continue;

      items.add( output.get( key ) );
    }

    return new IOResponse( items );
  }
  /**
   *
   * @param e
   * @return
   */
  protected IOResponse toNgResponse( Throwable e ){
    var items = UtilityHelper
      .toStream( unit.items() )
      .map(x -> x.toResponse( e ))
      .toList();

    return new IOResponse(items);
  }
  //endregion

  //region Class utility methods
  /**
   *
   * @return
   */
  protected int getTotalPointsCount(){
    var list = Stream
      .concat( words.stream(), bits.stream())
      .toList();

    var totalPoints = UtilityHelper
      .toStream( list )
      .map( x -> getPointsCount( x ) )
      .reduce( 0, ( a, b ) -> a + b );

    return totalPoints;
  }
  /**
   *
   * @param w
   * @param totalSize
   * @throws IOException
   */
  protected void encodePrologue( DataOutput w, int totalSize ) throws IOException {
    Coder.encodeHeader( w, totalSize );

    // Command
    w.write( ByteConverter.toBytes( getCode().value(), 2 ) );

    // Subcommand 00 means use children units (bit packed in word bits)
    w.write( ByteConverter.toBytes( 0, 2 ));

    // Number of word device blocks
    w.writeByte( words.size() );

    // Number of bit device blocks
    w.writeByte( bits.size() );
  }
  /**
   *
   * @param w
   * @param item
   * @throws IOException
   */
  protected void encodeItemHeader(DataOutput w, IPlcObject item ) throws IOException {
    // Word device number
    Coder.encodeDeviceNumber( w, item.address() );

    // Device code
    w.write( ( byte )item.device().value() );

    // Number of device points
    var size = getPointsCount( item );
    w.write( ByteConverter.toBytes( size, 2 ) );
  }
  //endregion
}
