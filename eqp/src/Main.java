import dispatcher.EquipmentCommandLineDispatcher;
import melsec.simulation.ServerOptions;

public class Main {
  public static void main(String[] args) {
    var options = ServerOptions
      .builder()
      .port( 8000 )
      .build();

    new EquipmentCommandLineDispatcher( options ).run();
  }
}