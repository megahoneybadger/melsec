package melsec.types;

import java.net.InetAddress;
import java.text.MessageFormat;

public record Endpoint( InetAddress address, int port ) {
  @Override
  public String toString(){
    var address = address().getHostAddress();
    var port = Integer.toString( port() );

    return MessageFormat.format( "{0}:{1}",address, port );
  }
}
