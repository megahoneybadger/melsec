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
  public byte[] handle() throws IOException {
    var subCommand = reader.readUnsignedShort();

    var wordBlockCount = reader.readUnsignedByte();
    var bitBlockCount = reader.readUnsignedByte();

    writeWords( reader, wordBlockCount );
    writeBits( reader, bitBlockCount );

    return reply( true );
  }
  /**
   *
   * @param r
   * @param wordBlockCount
   * @return
   * @throws IOException
   */
  private void writeWords( DataInput r, int wordBlockCount ) throws IOException {
    for( int i = 0; i < wordBlockCount; ++i ){
      int address = readDeviceNumber( r );
      var device = (WordDeviceCode) readDeviceCode( r );
      var points = r.readUnsignedShort();

      var buffer = new byte[ points * 2 ];
      r.readFully( buffer );

      memory.write( device, address, buffer );
    }
  }
  /**
   *
   * @param r
   * @param bitBlockCount
   * @return
   * @throws IOException
   */
  private void writeBits(DataInput r, int bitBlockCount ) throws IOException {
//    for( int i = 0; i < bitBlockCount; ++i ){
//      int address = readDeviceNumber( r );
//      var device = (BitDeviceCode) readDeviceCode( r );
//      var points = r.readUnsignedShort();
//
//      var buffer = memory.toBytes( device, address, points );
//
//      res = ByteConverter.concat( res, buffer );
//    }

    //write b100 true, b102 true
    // write b100 true, b102 true, b10F true, b110 true
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
