import dispatcher.EquipmentCommandLineDispatcher;
import melsec.simulation.ServerOptions;
import melsec.types.Endpoint;
import melsec.utils.UtilityHelper;

import java.net.UnknownHostException;

public class Main {
  public static void main(String[] args) throws UnknownHostException {

    var ep = UtilityHelper.coalesce(
      Endpoint.fromArgs( args ),
      Endpoint.getDefault() );

    var options = ServerOptions
      .builder()
      .endpoint( ep )
      .build();

    new EquipmentCommandLineDispatcher( options ).run();
  }
}