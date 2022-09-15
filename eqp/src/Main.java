import dispatcher.CommandLineDispatcher;
import melsec.simulation.Channel;
import melsec.simulation.Equipment;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {

    var eqp = new Equipment();

    var channel = new Channel( eqp.getMemory(), 8000 );
    channel.start();

    new CommandLineDispatcher( eqp ).run();

  }
}