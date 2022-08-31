package melsec.io.commands;

import melsec.exceptions.EncodingException;
import melsec.utils.EndianDataInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ICommand {

  //region Class properties
  /**
   *
   * @return
   */
  public abstract CommandCode code();
  //endregion

  //region Class 'Coding' methods
  /**
   *
   * @return
   */
  public byte[] encode() throws EncodingException {
    byte [] res = null;

    try( var bs = new ByteArrayOutputStream()){
      try( var ds = new DataOutputStream( bs )){
        encode( ds );

        res = bs.toByteArray();
      }
    }
    catch( Exception e ){
      throw new EncodingException();
    }

    res = ( null == res ) ? new byte[ 0 ] : res;

    return res;
  }
  /**
   *
   * @param ds
   * @throws IOException
   */
  protected abstract void encode( DataOutput ds ) throws IOException;
  /**
   *
   * @param buffer
   */
  public void decode( byte [] buffer ){
    try( var bs = new ByteArrayInputStream( buffer )){
      try( var ds = new EndianDataInputStream( bs )){
        decode( ds );
      }
    }
    catch( Exception e ){
      // throw new RtException( RtException.Code.CommandDecodingError, e.toString() );
    }
  }
  /**
   *
   * @param reader
   */
  protected abstract void decode( DataInput reader ) throws IOException;
  //endregion
}
