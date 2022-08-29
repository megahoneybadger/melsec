import dispatcher.CommandLineDispatcher;
import melsec.Config;
import melsec.Driver;
import melsec.log.ConsoleLogger;
import melsec.log.LogLevel;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;


public class Main {
  public static void main(String[] args) throws UnknownHostException {

    var config = Config
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger())
      .build();

    var driver = new Driver( config );

    new CommandLineDispatcher( driver ).run();
  }
}


