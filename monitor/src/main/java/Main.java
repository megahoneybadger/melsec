import dispatcher.ClientCommandLineDispatcher;
import melsec.bindings.*;
import melsec.bindings.files.BindingDeserializer;
import melsec.net.ClientOptions;
import melsec.net.EquipmentClient;
import melsec.scanner.EquipmentScanner;
import melsec.types.BitDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.io.IORequest;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;
import utils.Console;

import java.net.UnknownHostException;

import static melsec.types.WordDeviceCode.D;


public class Main {

  public static void main(String[] args) throws UnknownHostException, InterruptedException, BindingDeserializationException {

    var config = ClientOptions
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger( LogLevel.DEBUG ))
      .build();

    var client = new EquipmentClient( config );
    client.start();

    Thread.sleep( 500 );

    //addScanner( client );

//    System.out.println( "before ordinary request " + Thread.currentThread().getId());
//    client.exec( request );

    new ClientCommandLineDispatcher( client ).run();
  }

  //

  private static EquipmentScanner addScanner( EquipmentClient client ) throws BindingDeserializationException {
    return EquipmentScanner
      .builder()
      .binding( BindingDeserializer.read( ".resources/conf_big/file2.xml" ) )
      .region( BitDeviceCode.M, 0, 30000 )
      //.region( WordDeviceCode.D, 0, 2000 )
      //.region( BitDeviceCode.M, 0, 50000 )
//      .region( WordDeviceCode.W, 0, 300 )
      .timeout( 20 )
      .build( client );
  }
}

