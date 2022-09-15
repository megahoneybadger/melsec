package melsec.simulation.handlers;

import melsec.simulation.Memory;
import melsec.types.IDeviceCode;
import melsec.utils.Coder;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BaseHandler {

  //region Class members
  protected DataInput reader;
  protected Memory memory;
  //endregion

  //region Class initialization
  /**
   *
   * @param r
   */
  public BaseHandler(Memory m, DataInput r ){
    reader = r;
    memory = m;
  }
  //endregion

  //region Class 'Handle' methods
  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  public abstract byte[] handle() throws IOException;
  //endregion

  //region Class utility methods
  /**
   *
   * @param r
   * @return
   * @throws IOException
   */
  protected int readDeviceNumber(DataInput r ) throws IOException {
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
  protected IDeviceCode readDeviceCode(DataInput r ) throws IOException {
    return Coder.getDeviceCode( r.readUnsignedByte() );
  }
  //endregion
}
