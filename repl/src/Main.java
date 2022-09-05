import dispatcher.CommandLineDispatcher;
import melsec.Config;
import melsec.Driver;
import melsec.bindings.PlcBit;
import melsec.bindings.PlcU2;
import melsec.io.IORequest;
import melsec.log.ConsoleLogger;
import melsec.utils.Printer;
import utils.Console;

import java.net.UnknownHostException;

import static melsec.types.BitDeviceCode.B;
import static melsec.types.WordDeviceCode.W;


public class Main {


  public static void main(String[] args) throws UnknownHostException, InterruptedException {

    var config = Config
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger())
      .build();

    var driver = new Driver( config );

    Thread.sleep( 500 );

    new CommandLineDispatcher( driver ).run();
  }
}


