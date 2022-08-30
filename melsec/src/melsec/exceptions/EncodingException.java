package melsec.exceptions;

public class EncodingException extends Exception {
  public EncodingException(){
    super( "failed to encode command" );
  }
}
