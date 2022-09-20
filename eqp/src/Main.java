import dispatcher.EquipmentCommandLineDispatcher;
import melsec.simulation.net.EquipmentOptions;

public class Main {
  public static void main(String[] args) {
    var options = EquipmentOptions
      .builder()
      .port( 8000 )
      .build();

    new EquipmentCommandLineDispatcher( options ).run();
  }
}