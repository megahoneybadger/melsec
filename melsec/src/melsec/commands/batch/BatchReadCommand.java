package melsec.commands.batch;

import melsec.bindings.PlcBinary;
import melsec.types.CommandCode;
import melsec.commands.ICommand;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BadCompletionCodeException;
import melsec.types.exceptions.TooManyPointsException;
import melsec.types.io.IORequestUnit;
import melsec.types.io.IOResponse;
import melsec.types.io.IOResponseItem;
import melsec.types.io.IOResult;
import melsec.utils.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BatchReadCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  public static final int MAX_POINTS = 960;
  //endregion

  //region Class members
  /**
   *
    */
  private PlcBinary target;
  /**
   *
   */
  private PlcBinary result;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode getCode() {
    return CommandCode.BatchRead;
  }
  /**
   *
   * @return
   */
  private int getPointsCount(){
    var size = target.size();

    if( target.device() instanceof WordDeviceCode ){
      var extra = ( size % 2 == 0 ) ? 0 : 1;
      return size / 2 + extra;
    } else {
      var extra = ( size % 16 == 0 ) ? 0 : 1;
      return size / 16 + extra;
    }
  }
  //endregion

  //region Class initialization
  /**
   *
   */
  public BatchReadCommand( IORequestUnit u ){
    unit = u;

    target = ( PlcBinary )UtilityHelper
      .toList( u.items() )
      .get( 0 )
      .object();
  }
  /**
   *
   * @param unit
   * @return
   */
  public static List<ICommand> split( IORequestUnit unit ) {
    return UtilityHelper
      .toStream( unit.items() )
      .map( x -> ( ICommand )new BatchReadCommand( unit.with( x ) ) )
      .toList();
  }

  /**
   *
   * @return
   */
  @Override
  public String toString(){
    var shortId = id.substring( 0, 3 );

    return MessageFormat.format( "br#{0} {1}",
      shortId, Stringer.toRangeString( target ) );
  }
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param w
   */
  @Override
  protected void encode( DataOutput w ) throws IOException {
    Coder.encodeHeader( w, 0x0C );

    // Command
    w.write( ByteConverter.toBytes( getCode().value(), 2 ) );

    // Subcommand 00 means use children units (bit packed in word bits)
    w.write( ByteConverter.toBytes( 0, 2 ) );

    // Head device
    Coder.encodeDeviceNumber( w, target.address() );

    // Device code
    w.write( target.device().value() );

    // Number of device points
    var points = getPointsCount();

    if( points > MAX_POINTS )
      throw new TooManyPointsException();

    w.write( ByteConverter.toBytes( points, 2 ) );
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

    var buffer = new byte[ dataSize - 2/*completion code*/ ];
    r.readFully( buffer );

    result = ( PlcBinary ) Copier.withValue( target, buffer );
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
    var items = new ArrayList<IOResponseItem>();

    items.add( new IOResponseItem(
      unit.operation(), target, IOResult.create( result ) ) );

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
