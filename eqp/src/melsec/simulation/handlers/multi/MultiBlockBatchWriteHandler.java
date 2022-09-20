package melsec.simulation.handlers.multi;

import melsec.exceptions.InvalidRangeException;
import melsec.simulation.Memory;
import melsec.simulation.handlers.BaseHandler;
import melsec.simulation.handlers.RequestBlock;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class MultiBlockBatchWriteHandler extends BaseHandler {

  //region Class initialization
  /**
   *
   * @param r
   */
  public MultiBlockBatchWriteHandler(Memory m, DataInput r ){
    super( m, r );
  }
  //endregion

  //region Class 'Handle' methods
  /**
   *
   * @return
   * @throws IOException
   */
  @Override
  public byte[] handle() throws IOException, InvalidRangeException {
    var subCommand = reader.readUnsignedShort();

    var wordBlockCount = reader.readUnsignedByte();
    var bitBlockCount = reader.readUnsignedByte();

    write( reader, wordBlockCount );
    write( reader, bitBlockCount );

    return reply( true );
  }
  /**
   *
   * @param r
   * @param blockCount
   * @return
   * @throws IOException
   */
  private void write( DataInput r, int blockCount ) throws IOException, InvalidRangeException {
    for( int i = 0; i < blockCount; ++i ){
      var block = RequestBlock.decodeWrite( r );

      memory.setBytes( block );
    }
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
