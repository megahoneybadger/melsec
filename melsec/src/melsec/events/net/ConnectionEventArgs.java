package melsec.events.net;

import melsec.net.Endpoint;
import melsec.events.IEventArgs;

import java.text.MessageFormat;

public record ConnectionEventArgs( Endpoint endpoint, String id ) implements IEventArgs {

  @Override
  public String toString(){
    var address = endpoint.address().getHostAddress().toString();
    var port = Integer.toString( endpoint.port() );

    return MessageFormat.format( "{0}:{1}",address, port, id );
  }
}
