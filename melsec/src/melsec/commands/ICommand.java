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

  //region Class constants
  /**
   *
   */
  public static final int HEADER_LENGTH = 9;
  //endregion

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
  /**
   *
   * @param w
   * @throws IOException
   */
  protected void encodeTitle( DataOutput w ) throws IOException {
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
  protected void encodeHeader( DataOutput w, int len ) throws IOException {
    encodeTitle( w );

    // Request data count
    w.write( toBytes( len, 2 ) );

    // CPU monitoring timer
    w.writeByte( 0x10 );
    w.writeByte( 0x00 );
  }
  /**
   *
   * @param v
   * @param bytesNo
   * @return
   */
  protected byte[] toBytes( int v, int bytesNo ){
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
