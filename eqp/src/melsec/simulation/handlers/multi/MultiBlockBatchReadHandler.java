package melsec.simulation.handlers.multi;

import melsec.types.exceptions.InvalidRangeException;
import melsec.simulation.Memory;
import melsec.simulation.handlers.BaseHandler;
import melsec.simulation.handlers.RequestBlock;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class MultiBlockBatchReadHandler extends BaseHandler {

  //region Class initialization
  /**
   *
   * @param r
   */
  public MultiBlockBatchReadHandler(Memory m, DataInput r ){
    super( m, r );
  }
  //endregion

  //region Class 'Handle' methods
  @Override
  public byte[] handle() throws IOException, InvalidRangeException {
    var subCommand = reader.readUnsignedShort();

    var wordBlockCount = reader.readUnsignedByte();
    var bitBlockCount = reader.readUnsignedByte();

    var words = read( reader, wordBlockCount );
    var bits = read( reader, bitBlockCount );

    return reply( bits, words );
  }
  /**
   *
   * @param r
   * @param blockCount
   * @return
   * @throws IOException
   */
  private byte [] read(DataInput r, int blockCount ) throws IOException, InvalidRangeException {
    byte [] res = null;

    for( int i = 0; i < blockCount; ++i ){
      var block = RequestBlock.decodeRead( r );

      var buffer = memory.getBytes( block );

      res = ByteConverter.concat( res, buffer );
    }

    return res;
  }

  /**
   *
   * @param bits
   * @param words
   * @return
   */
  private byte [] reply( byte [] bits, byte [] words ){
    try( var bs = new ByteArrayOutputStream()){
      try( var w = new DataOutputStream( bs )){

        Coder.encodeTitle( w );

        int size =
          ( ( null != bits ) ? bits.length : 0 ) +
          ( ( null != words ) ? words.length : 0 ) +
          2 /*Completion code*/;

        w.write( ByteConverter.toBytes( size, 2 )  );

        var completionCode = 0;
        w.write( ByteConverter.toBytes( completionCode, 2 ) );

        if( null != words ){
          w.write( words );
        }

        if( null != bits ){
          w.write( bits );
        }

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
