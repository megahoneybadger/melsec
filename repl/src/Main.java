import dispatcher.ClientCommandLineDispatcher;
import melsec.ClientOptions;
import melsec.EquipmentClient;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;

import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


public class Main {

  public static void main(String[] args) throws UnknownHostException, InterruptedException {



    var config = ClientOptions
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger( LogLevel.DEBUG ))
      .build();

    var driver = new EquipmentClient( config );
    driver.start();

    Thread.sleep( 500 );

//    var st = PlcStruct
//      .builder( WordDeviceCode.W, 0x100, "Glass" )
//      .offset( 2 )
//      .u2( 101 )
//      .u2( 27894 )
//      .u2( 31254 )
//      .offset( 3 )
//      .i2( ( short )-1456 )
//      .offset( 1 )
//      .i2( ( short )5567 )
//      .string( 4, "helloworld" )
//      .build();
////
//    var request = IORequest
//      .builder()
//      .write( st )
//      .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
//      .build();
////
//    driver.exec( request );

    new ClientCommandLineDispatcher( driver ).run();
  }
}


