import dispatcher.CommandLineDispatcher;
import melsec.Config;
import melsec.Driver;
import melsec.bindings.PlcStruct;
import melsec.events.commands.ICommandAfterSendEvent;
import melsec.events.commands.ICommandBeforeSendEvent;
import melsec.exceptions.DriverNotRunningException;
import melsec.exceptions.EncodingException;
import melsec.io.IORequest;
import melsec.log.ConsoleLogger;
import melsec.log.LogLevel;
import melsec.types.WordDeviceCode;
import utils.Console;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class Main {


  public static void main(String[] args) throws UnknownHostException, InterruptedException, DriverNotRunningException, EncodingException {

    var z = ( short )65080;

    var config = Config
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger( LogLevel.DEBUG ))
      .build();

    var driver = new Driver( config );
    driver.start();

    Thread.sleep( 500 );

//    var st = PlcStruct
//      .builder( WordDeviceCode.W, 0x100, "Glass" )
//      .u2( 100 )
//      .u2( 27894 )
//      .u2( 31254 )
//      .offset( 3 )
//      .i2( ( short )-1456 )
//      .offset( 1 )
//      .i2( ( short )54567 )
//      .build();
////
//    var request = IORequest
//      .builder()
//      .write( st )
//      .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
//      .build();
//
//    driver.exec( request );

    new CommandLineDispatcher( driver ).run();
  }
}


