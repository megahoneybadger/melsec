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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    var c = ( code() == CommandCode.MultiBlockBatchWrite ) ? "w" : "r";
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
}
