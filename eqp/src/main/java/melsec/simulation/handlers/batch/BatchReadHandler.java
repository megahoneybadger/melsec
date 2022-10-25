package melsec.simulation.handlers.batch;

import melsec.simulation.Memory;
import melsec.simulation.handlers.BaseHandler;
import melsec.simulation.handlers.RequestBlock;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BatchReadHandler extends BaseHandler {

  //region Class initialization
  /**
   *
   * @param r
   */
  public BatchReadHandler( Memory m, DataInput r ){
    super( m, r );
  }
  //endregion

  //region Class 'Handle' methods
  @Override
  public byte[] handle() throws IOException, InvalidRangeException {
    var subCommand = reader.readUnsignedShort();

    var address = Coder.decodeDeviceAddress( reader );
    var device = Coder.getDeviceCode( reader.readUnsignedByte());
    var points = reader.readUnsignedShort();

    var rb = new RequestBlock( device, address, points );

    var bytes = memory.getBytes( rb );

    return reply( bytes );
  }
  /**
   *
   * @param data
   * @return
   */
  private byte [] reply( byte [] data ){
    try( var bs = new ByteArrayOutputStream()){
      try( var w = new DataOutputStream( bs )){
        Coder.encodeTitle( w );

        int size =  ( ( null != data ) ? data.length : 0 ) + 2 /*Completion code*/;
        w.write( ByteConverter.toBytes( size, 2 )  );

        var completionCode = 0;
        w.write( ByteConverter.toBytes( completionCode, 2 ) );

        if( null != data ){
          w.write( data );
        }

        return bs.toByteArray();
      }
    }
    catch( Exception e ){
      //throw new EncodingException( this, e );
    }

    return ByteConverter.empty();
  }
  /**
   *
   * @param w
   * @param data
   */
  private void replyBits( DataOutput w, byte[] data ) throws IOException {
    Coder.encodeTitle( w );

    int bytesLength = ( null == data ) ? 0 : data.length;

    // number of device points
    var extra = ( 0 != ( bytesLength % 8 ) );

    var size = ( bytesLength + ( ( extra ) ? 1 : 0 ) + 2 /*CompletionCode*/ );
    w.write( ByteConverter.toBytes( size, 2 )  );
  }
  /**
   *
   * @param w
   * @param data
   */
  private void replyWords( DataOutput w, byte[] data ){

  }
  //endregion
}
