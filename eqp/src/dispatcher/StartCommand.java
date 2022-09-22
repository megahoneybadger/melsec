package dispatcher;

import melsec.simulation.EquipmentServer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class StartCommand extends BaseCommand  {
  public final static String COMMAND = "start";

  private EquipmentServer server;

  public StartCommand( EquipmentServer s ){
    super( null );
    server = s;
  }

  @Override
  public void exec( List<String> args ){
    try {
      server.start();
    } catch(IOException e) {
      System.err.println( MessageFormat
        .format( "Failed to start eqp server. {0}", e.getMessage() ) );
    }
  }
}
