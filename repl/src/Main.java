import dispatcher.CommandLineDispatcher;
import melsec.Config;
import melsec.Driver;
import melsec.bindings.PlcBit;
import melsec.bindings.PlcU1;
import melsec.bindings.PlcU2;
import melsec.io.IORequest;
import melsec.log.ConsoleLogger;
import melsec.types.BitDeviceCode;

import java.net.UnknownHostException;

import static melsec.types.BitDeviceCode.*;
import static melsec.types.WordDeviceCode.*;


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
    driver.start();

    Thread.sleep( 500 );

    Object sync = new Object();

    var request = IORequest
      .builder()
      .read( new PlcBit( B, 100, "RecvGlassRequestBit1"  ) )
      .read( new PlcBit( B, 200, "RecvGlassRequestBit2"  ) )
      .read( new PlcU2( W, 300, "GlassId" ) )
      .write( new PlcBit( B, 401, true, "RecvGlassReplyBit" ) )
//      .read( new PlcU2( W, 400, "Something" ) )
      .complete( x -> {
        synchronized( sync ){
          for( var item : x.items() ){
            System.out.println( item );
          }
        }

      } )
      .build();

    driver.exec( request );

    new CommandLineDispatcher( driver ).run();




  }
}


