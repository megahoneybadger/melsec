package melsec.simulation.handlers.random;

import melsec.bindings.PlcBit;
import melsec.simulation.Memory;
import melsec.simulation.handlers.BaseHandler;
import melsec.simulation.handlers.RequestBlock;
import melsec.types.BitDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.*;

public class RandomWriteHandler extends BaseHandler {

  //region Class initialization
  /**
   *
   * @param r
   */
  public RandomWriteHandler( Memory m, DataInput r ){
    super( m, r );
  }
  //endregion

  //region Class 'Handle' methods
  @Override
  public byte[] handle() throws IOException, InvalidRangeException {
    var subCommand = reader.readUnsignedShort();

    var bitBlockCount = reader.readUnsignedByte();

    for( int i = 0; i < bitBlockCount; ++i ){
      var block = RequestBlock.decodeRandomWrite( reader );
      var v = block.buffer()[ 0 ] == 1;
      var bit = new PlcBit( ( BitDeviceCode ) block.device(), block.address(), v );

      memory.write( bit );
    }

    return reply( true );
  }
  /**
   *
   * @param res
   * @return
   */
  private byte [] reply( boolean res ){
    try( var bs = new ByteArrayOutputStream()){
      try( var w = new DataOutputStream( bs )){

        Coder.encodeTitle( w );

        w.write( ByteConverter.toBytes( 2, 2 )  );

        var iCompletionCode = 0;
        w.write( ByteConverter.toBytes( iCompletionCode, 2 ) );

        return bs.toByteArray();
      }
    }
    catch( Exception e ){
      //throw new EncodingException( this, e );
    }

    return ByteConverter.empty();
  }
  //endregion
}
