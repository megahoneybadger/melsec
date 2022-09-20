package melsec.simulation.handlers;

import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.Coder;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record RequestBlock(IDeviceCode device, int address, int points, byte [] buffer ) {

  public RequestBlock(IDeviceCode device, int address, int points ){
    this( device, address, points, null );
  }

  //region Class initialization
  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  public static RequestBlock decodeRead( DataInput r ) throws IOException {
    int address = readDeviceNumber( r );
    var device = readDeviceCode( r );
    var points = r.readUnsignedShort();

    return new RequestBlock( device, address, points, null );
  }

  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  public static RequestBlock decodeWrite( DataInput r ) throws IOException {
    int address = readDeviceNumber( r );
    var device = readDeviceCode( r );
    var points = r.readUnsignedShort();

    var buffer = new byte[ points * 2 ];
    r.readFully( buffer );

    return new RequestBlock( device, address, points, buffer );
  }
  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  protected static int readDeviceNumber(DataInput r ) throws IOException {
    var headDevice = new byte[ 3 ];
    r.readFully( headDevice );
    var bufferAddress = new byte [ 4 ];
    System.arraycopy( headDevice, 0, bufferAddress, 0, 3 );

    var bb = ByteBuffer
      .wrap( bufferAddress )
      .order( ByteOrder.LITTLE_ENDIAN );

    return bb.getInt();

  }
  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  protected static IDeviceCode readDeviceCode(DataInput r ) throws IOException {
    return Coder.getDeviceCode( r.readUnsignedByte() );
  }
  //endregion
}
