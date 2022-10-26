import dispatcher.ClientCommandLineDispatcher;
import melsec.bindings.*;
import melsec.bindings.files.BindingDeserializer;
import melsec.net.ClientOptions;
import melsec.net.EquipmentClient;
import melsec.scanner.EquipmentScanner;
import melsec.types.BitDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.types.events.client.IClientStartedEvent;
import melsec.types.events.client.IClientStoppedEvent;
import melsec.types.events.net.IConnectionConnectingEvent;
import melsec.types.events.net.IConnectionDisposedEvent;
import melsec.types.events.net.IConnectionEstablishedEvent;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.io.IORequest;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;
import utils.Console;

import java.net.UnknownHostException;
import java.util.Scanner;

import static melsec.types.WordDeviceCode.D;

public class Main {

  public static void main(String[] args) throws UnknownHostException, BindingDeserializationException, InterruptedException {

    var config = ClientOptions
      .builder()
      .address( "127.0.0.1" )
      .port( 8000 )
      .loggers(
        new ConsoleLogger( LogLevel.SCAN ))
      .build();

    var client = new EquipmentClient( config );
    client.start();

    Thread.sleep( 500 );

    //addScanner( client );

    new ClientCommandLineDispatcher( client ).run();
  }

  private static void addScanner( EquipmentClient client ) throws BindingDeserializationException {
    EquipmentScanner
      .builder()
      .changed( x -> x.changes().forEach( y -> System.out.println( y ) ) )
      .binding( BindingDeserializer.read( ".resources/conf_big/file2.xml" ) )
      .region( BitDeviceCode.M, 0, 1000 )
      .build( client );
  }
}


//