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
        new ConsoleLogger( LogLevel.SCAN ))
      .build();

    var client = new EquipmentClient( config );
    client.start();

    Thread.sleep( 500 );

    addScanner( client );

//    System.out.println( "before ordinary request " + Thread.currentThread().getId());
//    client.exec( request );

    new ClientCommandLineDispatcher( client ).run();
  }

  //

  private static EquipmentScanner addScanner( EquipmentClient client ) throws BindingDeserializationException {
    return EquipmentScanner
      .builder()
      .binding( BindingDeserializer.read( ".resources/conf_big/file1.xml" ) )
      //.region( BitDeviceCode.M, 15000, 30000 )
      .region( BitDeviceCode.B, 0, 50000 )
      .region( BitDeviceCode.M, 0, 50000 )
//      .region( WordDeviceCode.W, 0, 300 )
      .timeout( 20 )
      .build( client );

//    var request = IORequest
//      .builder()
////      .read( new PlcBit( B, 100, "RecvGlassRequestBit1" ) )
////      .read( new PlcBit( B, 200, "RecvGlassRequestBit2" ) )
////      .read( new PlcU2( W, 300, "GlassId" ) )
//      //.write( new PlcBit( B, 401, true, "RecvGlassReplyBit" ) )
////      .write( new PlcI4( D, 401, -78945, "a1" ) )
//      .read(new PlcBinary( D, 0, 5 ))
////      .read(new PlcBinary( D, 50, 150 ))
////      .read(new PlcBinary( D, 300, 50))
//      //.read(new PlcBinary( B, 0, 50 ))
////      .read( new PlcU2( W, 400, "Something" ) )
////      .write( new PlcU4( D, 402, 78945l, "a2" ) )
////      .write( new PlcU4( D, 407, 5l, "a3" ) )
//      .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
//      .build();
  }
}


