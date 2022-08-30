package melsec.io.commands;

import melsec.exceptions.EncodingException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
  protected abstract void encode( DataOutputStream ds ) throws IOException;
  //endregion
}
