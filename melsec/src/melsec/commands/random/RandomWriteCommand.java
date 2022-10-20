package melsec.commands.random;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBinary;
import melsec.bindings.PlcBit;
import melsec.commands.ICommand;
import melsec.commands.multi.MultiBlockBatchWriteCommand;
import melsec.types.CommandCode;
import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BadCompletionCodeException;
import melsec.types.exceptions.TooManyPointsException;
import melsec.types.io.*;
import melsec.utils.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomWriteCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  public static final int MAX_POINTS = 188;
  //endregion

  //region Class members
  /**
   *
   */
  protected List<PlcBit> bits;
  /**
   *
   */
  protected List<PlcBit> results;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode getCode() {
    return CommandCode.RandomWrite;
  }
  //endregion

  //region Class initialization
  /**
   *
   */
  public RandomWriteCommand( IORequestUnit u ){
    unit = u;

    bits = UtilityHelper
      .toStream( u.items() )
      .filter( x -> x.object().type() == DataType.Bit )
      .map( x -> ( PlcBit )x.object() )
      .collect( Collectors.toList() );
  }
  /**
   *
   * @param unit
   * @return
   */
  public static List<ICommand> split( IORequestUnit unit ) {
    var res = new ArrayList<ICommand>();
    var items = new ArrayList<IORequestItem>();

    var count = 0;

    for( var item: unit.items() ){
      if( count >= MAX_POINTS ){
        res.add( new RandomWriteCommand( unit.with( items ) ) );

        items.clear();
        count = 0;
      }

      items.add( item );
      count++;
    }

    if( items.size() > 0 ){
      res.add( new RandomWriteCommand( unit.with( items ) ) );
    }

    return res;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString(){
    var shortId = id.substring( 0, 3 );

    return MessageFormat.format( "rw#{0} [{1}]",  shortId, bits.size() );
  }
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param w
   */
  @Override
  protected void encode( DataOutput w ) throws IOException {
    if( bits.size() > MAX_POINTS )
      throw new TooManyPointsException();

    int totalSize = 2 + 5 +  bits.size() * 5;

    Coder.encodeHeader( w, totalSize );

    // Command
    w.write( ByteConverter.toBytes( getCode().value(), 2 ) );

    // Subcommand
    w.writeByte( 0x01 );
    w.writeByte( 0x00 );

    // Number of bit device blocks
    w.writeByte( bits.size() );

    for( var item: bits ) {
      // Word device number
      Coder.encodeDeviceAddress( w, item.address() );

      // Device code
      w.write( ( byte )item.device().value() );

      var b = ( byte )( ( item.value() ) ? 1 : 0 );
      w.write( b );
    }
  }
  //endregion

  //region Class 'Decoding' methods
  /**
   *
   * @param r
   */
  @Override
  protected void decode( DataInput r ) throws IOException, BadCompletionCodeException {
    r.skipBytes( 7 );

    var dataSize = r.readUnsignedShort();
    var completionCode = r.readUnsignedShort();

    if( 0 != completionCode )
      throw new BadCompletionCodeException( completionCode );

    results = bits
      .stream()
      .toList();
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
    var items = UtilityHelper
      .toStream( unit.items() )
      .map( x -> x.toResponse( x.object() ) )
      .toList();

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
