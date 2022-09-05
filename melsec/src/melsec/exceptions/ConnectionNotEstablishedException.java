package melsec.exceptions;

import melsec.net.Endpoint;
import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class ConnectionNotEstablishedException extends BaseException {
  public ConnectionNotEstablishedException( Endpoint ep ){

     super( ErrorCode.ConnectionNotEstablished,
       MessageFormat.format( "Connection with {0}:{1} is not established",
         ep.address().getHostAddress(), Integer.toString( ep.port() )));

  }
}
