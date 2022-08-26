import dispatcher.CommandLineDispatcher;
import melsec.config.PlcConfig;
import melsec.Driver;

import java.net.UnknownHostException;


public class Main {
  public static void main(String[] args) throws UnknownHostException {

    var config = PlcConfig
      .builder()
      .address( "127.21.5.7" )
      .port( 5000 )
      .build();

    var driver = new Driver( config );

    new CommandLineDispatcher( driver ).run();
  }
}


