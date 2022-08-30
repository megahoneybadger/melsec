package melsec.io.commands;

import java.io.DataOutputStream;
import java.io.IOException;

public class Coder {

  //region Class 'Encoding' methods
  /**
   *
   * @param writer
   * @throws IOException
   */
  public static void encodeTitle( DataOutputStream writer ) throws IOException {
    // Subheader
    writer.writeByte( 0x50 );
    writer.writeByte( 0x00 );

    // Network No
    writer.writeByte( 0x00 );

    // Plc No
    writer.writeByte( 0xFF );

    // Request destination module IO No
    writer.writeByte( 0xFF );
    writer.writeByte( 0x03 );

    // Request destination module station No
    writer.writeByte( 0x00 );
  }
  /**
   *
   * @param writer
   * @param len
   * @throws IOException
   */
  public static void encodeHeader( DataOutputStream writer, int len ) throws IOException {
    encodeTitle( writer );

    // Request data count
    writer.write( toBytes( len, 2 ) );
  }
  /**
   *
   * @param v
   * @param bytesNo
   * @return
   */
  public static byte[] toBytes( int v, int bytesNo ){
    var arr = new byte[ bytesNo ];

    for( int i = 0; i < bytesNo; ++i ){
      int iNextByte = v & 255;
      arr[ i ] = ( byte )iNextByte;

      v >>= 8;
    }

    return arr;
  }
  //endregion

}
