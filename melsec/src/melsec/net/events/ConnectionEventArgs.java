package melsec.net.events;

import melsec.net.Endpoint;

import java.text.MessageFormat;

public record ConnectionEventArgs( Endpoint endpoint, String id ) {

  @Override
  public String toString(){
    var address = endpoint.address().getHostAddress().toString();
    var port = Integer.toString( endpoint.port() );

    return MessageFormat.format( "{0}:{1}",address, port, id );
  }
}
