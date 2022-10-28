import dispatcher.ClientCommandLineDispatcher;
import melsec.bindings.files.BindingDeserializer;
import melsec.net.ClientOptions;
import melsec.EquipmentClient;
import melsec.scanner.EquipmentScanner;
import melsec.types.BitDeviceCode;
import melsec.types.Endpoint;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;
import melsec.utils.UtilityHelper;

import java.net.UnknownHostException;

public class Main {

  public static void main(String[] args) throws UnknownHostException, BindingDeserializationException, InterruptedException {

    var ep = UtilityHelper.coalesce(
      Endpoint.fromArgs( args ),
      Endpoint.getDefault() );

    //mvn -pl monitor compile exec:java -Dexec.mainClass="Main" -Dexec.arguments="127.0.0.1:8000"
    //https://central.sonatype.org/publish/publish-maven/
    var config = ClientOptions
      .builder()
      .endpoint( ep )
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