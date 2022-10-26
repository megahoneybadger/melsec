package melsec.types;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

public record Endpoint( InetAddress address, int port ) {
  @Override
  public String toString(){
    var address = address().getHostAddress();
    var port = Integer.toString( port() );

    return MessageFormat.format( "{0}:{1}",address, port );
  }

  public static Endpoint getDefault() throws UnknownHostException {
    return new Endpoint( InetAddress.getByName( "127.0.0.1" ), 8000 );
  }

  public static Endpoint fromArgs( String [] args ){
    if( null == args || 0 == args.length )
      return null;

    var s = args[ 0 ];
    var index = s.indexOf( ':' );

    if( index == -1 )
      return null;

    try{
      var ip = InetAddress.getByName(s.substring( 0, index ));
      var port = Integer.parseInt( s.substring( index + 1 ) );

      return new Endpoint( ip, port );
    }
    catch( Exception exc ){

    }

    return null;
  }
}
