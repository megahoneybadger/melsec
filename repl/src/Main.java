import dispatcher.CommandLineDispatcher;
import melsec.Config;
import melsec.Driver;
import melsec.events.commands.ICommandAfterSendEvent;
import melsec.events.commands.ICommandBeforeSendEvent;
import melsec.exceptions.DriverNotRunningException;
import melsec.exceptions.EncodingException;
import melsec.log.ConsoleLogger;
import melsec.log.LogLevel;

import java.net.UnknownHostException;


public class Main {


  public static void main(String[] args) throws UnknownHostException, InterruptedException, DriverNotRunningException, EncodingException {

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
//      .string( 2 )
//      .offset( 1 )
//      .string( 4 )
//      .build();
//
//    var request = IORequest
//      .builder()
//      .read( st )
//      .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
//      .build();

//    driver.exec( request );

    new CommandLineDispatcher( driver ).run();
  }
}


