package melsec.commands;

import melsec.exceptions.BadCompletionCodeException;
import melsec.exceptions.DecodingException;
import melsec.exceptions.EncodingException;
import melsec.io.IORequestUnit;
import melsec.io.IOResponse;
import melsec.utils.EndianDataInputStream;
import melsec.utils.UtilityHelper;

import java.io.*;
import java.security.SecureRandom;

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
    id = Integer
      .valueOf( new SecureRandom().nextInt( 1000 ))
      .toString();
  }
  /**
   *
   * @return
   */
  public abstract ICommand copy();
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
  public IOResponse toResponse(){
    return null;
  }
  /**
   *
   */
  public void complete(){
    if( null != unit && null != unit.handler() ){
      unit.handler().complete( toResponse() );
    }
  }
  /**
   *
   * @param e
   */
  public void complete( Throwable e ){
    if( null == e ){
      complete();
    } else if( null != unit ) {
      var items = UtilityHelper
        .toList( unit.items() )
        .stream()
        .map( x -> x.toResponse( e ) )
        .toList();

      var response = new IOResponse( items );

      if( null != unit.handler() ){
        unit.handler().complete( response );
      }
    }
  }
  //endregion
}
