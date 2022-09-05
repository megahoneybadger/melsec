package melsec.commands;

import melsec.types.DataType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Coder {

  //region Class constants
  /**
   *
   */
  public static final int HEADER_LENGTH = 9;
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param writer
   * @throws IOException
   */
  public static void encodeTitle( DataOutput writer ) throws IOException {
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
  public static void encodeHeader( DataOutput writer, int len ) throws IOException {
    encodeTitle( writer );

    // Request data count
    writer.write( toBytes( len, 2 ) );

    // CPU monitoring timer
    writer.writeByte( 0x10 );
    writer.writeByte( 0x00 );
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

  //region Class 'Decoding' methods
  /**
   *
   * @param buffer
   * @return
   */
  public static int decodeLength( byte [] buffer ){
    int iHByteIndex = 8;
    int iRes = Coder.toUByte( buffer, iHByteIndex );
    iRes <<= 8;

    int iLowByte = Coder.toUByte( buffer, iHByteIndex - 1 );

    iRes += iLowByte;

    return iRes;
  }
  /**
   *
   * @param b
   * @return
   */
  public static int toUByte( byte b ){
    return ( b & 0xFF );
  }
  /**
   *
   * @param buffer
   * @param index
   * @return
   */
  public static int toUByte( byte [] buffer, int index ){
    return toUByte( buffer[ index ] );
  }
  /**
   *
   * @param s
   * @return
   */
  public static int toUShort( short s ){
    return ( s & 0xFFFF );
  }
  /**
   *
   * @param reader
   * @param type
   * @return
   * @throws IOException
   */
  public static Object decodeValue( DataInput reader, DataType type ) throws IOException {
    return switch( type ){
      case U1 -> reader.readUnsignedByte();
      case U2 -> reader.readUnsignedShort();

//      case I2 -> bb.getShort( index );
//      case I4 -> bb.getInt( index );
//      case I8 -> bb.getLong( index );
//
//      case F4 -> bb.getFloat( index );
//      case F8 -> bb.getDouble( index );

      default -> null;
    };

//    switch( o.type() ){
//      case I2:
//        return bb.getShort( index );
//
//      case I4:
//        return bb.getInt( index );
//
//      case I8:
//        return bb.getLong( index );
//
//      case F4:
//        return bb.getFloat( index );
//
//      case F8:
//        return bb.getDouble( index );
//
////      case String:
////        bb.position( index );
////        byte [] sarr = new byte[ o.size() * 2 ];
////        bb.get( sarr );
////        return new String( sarr );
//
////      case Bin:
////        bb.position( index );
////        byte [] barr = new byte[ o.size() * 2 ];
////        bb.get( barr );
////        return barr;
//    }


    //return null;
  }
  //endregion

}
