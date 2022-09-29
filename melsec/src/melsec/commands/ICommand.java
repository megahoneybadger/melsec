package melsec.commands;

import melsec.types.CommandCode;
import melsec.types.exceptions.BadCompletionCodeException;
import melsec.types.exceptions.DecodingException;
import melsec.types.exceptions.EncodingException;
import melsec.types.io.IORequestUnit;
import melsec.types.io.IOResponse;
import melsec.utils.EndianDataInputStream;
import melsec.utils.UtilityHelper;

import java.io.*;
import java.util.UUID;

public abstract class ICommand {

  //region Class members
  /**
   *
   */
  protected String id;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public abstract CommandCode code();
  /**
   *
   * @return
   */
  public String id(){
    return id;
  }
  /**
   *
   */
  protected IORequestUnit unit;
  //endregion

  //region Class initialization
  /**
   *
   */
  public ICommand(){
    id = UUID.randomUUID().toString();
  }
  //endregion

  //region Class 'Encoding' methods
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
      throw new EncodingException( this, e );
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

  //endregion

  //region Class 'Decoding' methods
  /**
   *
   * @param buffer
   */
  public void decode( byte [] buffer ) throws DecodingException {
    try( var bs = new ByteArrayInputStream( buffer )){
      try( var ds = new EndianDataInputStream( bs )){
        decode( ds );

        if( bs.available() > 0 )
          throw new Exception( "Invalid stream reading alignment." );
      }
    }
    catch( Exception e ){
      throw new DecodingException( this, e );
    }
  }
  /**
   *
   * @param reader
   */
  protected abstract void decode( DataInput reader ) throws IOException, BadCompletionCodeException;
  //endregion

  //region Class 'Response' methods
  /**
   *
   * @return
   */
  public abstract IOResponse toResponse( Throwable e );
  //endregion
}
