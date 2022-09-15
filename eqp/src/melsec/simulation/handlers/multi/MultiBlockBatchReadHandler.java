package melsec.simulation.handlers.multi;

import melsec.simulation.Memory;
import melsec.simulation.handlers.BaseHandler;
import melsec.types.BitDeviceCode;
import melsec.types.WordDeviceCode;
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
  public byte[] handle() throws IOException {
    var subCommand = reader.readUnsignedShort();

    var wordBlockCount = reader.readUnsignedByte();
    var bitBlockCount = reader.readUnsignedByte();

    var words = readWords( reader, wordBlockCount );
    var bits = readBits( reader, bitBlockCount );

    return reply( bits, words );
  }
  /**
   *
   * @param r
   * @param wordBlockCount
   * @return
   * @throws IOException
   */
  private byte [] readWords( DataInput r, int wordBlockCount ) throws IOException {
    byte [] res = null;

    for( int i = 0; i < wordBlockCount; ++i ){
      int address = readDeviceNumber( r );
      var device = (WordDeviceCode) readDeviceCode( r );
      var points = r.readUnsignedShort();

      var buffer = memory.read( device, address, points );

      res = ByteConverter.concat( res, buffer );
    }

    return res;
  }
  /**
   *
   * @param r
   * @param bitBlockCount
   * @return
   * @throws IOException
   */
  private byte [] readBits(DataInput r, int bitBlockCount ) throws IOException {
    byte [] res = null;

    for( int i = 0; i < bitBlockCount; ++i ){
      int address = readDeviceNumber( r );
      var device = (BitDeviceCode) readDeviceCode( r );
      var points = r.readUnsignedShort();

      var buffer = memory.read( device, address, points );

      res = ByteConverter.concat( res, buffer );
    }

    //write b100 true, b102 true
    // write b100 true, b102 true, b10F true, b110 true

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

        var iCompletionCode = 0;
        w.write( ByteConverter.toBytes( iCompletionCode, 2 ) );

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
