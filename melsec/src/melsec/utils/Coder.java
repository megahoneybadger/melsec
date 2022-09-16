package melsec.utils;

import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

import java.io.DataOutput;
import java.io.IOException;

public class Coder {

  //region Class constants
  /**
   *
   */
  public static final int HEADER_LENGTH = 9;
  //endregion

  //region Class public methods
  /**
   *
   * @param w
   * @throws IOException
   */
  public static void encodeTitle( DataOutput w ) throws IOException {
    // Subheader
    w.writeByte( 0x50 );
    w.writeByte( 0x00 );

    // Network No
    w.writeByte( 0x00 );

    // Plc No
    w.writeByte( 0xFF );

    // Request destination module IO No
    w.writeByte( 0xFF );
    w.writeByte( 0x03 );

    // Request destination module station No
    w.writeByte( 0x00 );
  }
  /**
   *
   * @param w
   * @param len
   * @throws IOException
   */
  public static void encodeHeader( DataOutput w, int len ) throws IOException {
    encodeTitle( w );

    // Request data count
    w.write( ByteConverter.toBytes( len, 2 ) );

    // CPU monitoring timer
    w.writeByte( 0x10 );
    w.writeByte( 0x00 );
  }
  /**
   *
   * @param w
   * @param address
   */
  public static void encodeDeviceNumber( DataOutput w, int address ) throws IOException {
    if( address < 0 || address > ( 1 << 24 ) - 1 )
      throw new IOException( "Invalid device address" );

    w.write( ByteConverter.toBytes( address, 3 ) );
  }
  /**
   *
   * @param header
   * @return
   */
  public static int getCommandBodySize( byte [] header ){
    int iHByteIndex = 8;
    int iRes = header[ iHByteIndex ] & 0xFF;

    iRes <<= 8;

    int iLowByte = header[ iHByteIndex - 1 ] & 0xFF;

    iRes += iLowByte;

    return iRes;
  }
  /**
   *
   * @param code
   * @return
   */
  public static IDeviceCode getDeviceCode( int code ){
    for ( var device : BitDeviceCode.values()) {
      if( device.value() == code )
        return device;
    }

    for ( var device : WordDeviceCode.values()) {
      if( device.value() == code )
        return device;
    }

    return null;
  }
  //endregion
}
