package dispatcher;

import melsec.simulation.EquipmentServer;
import melsec.utils.UtilityHelper;

import java.text.MessageFormat;
import java.util.List;

public class StopCommand extends BaseCommand  {
  public final static String COMMAND = "stop";

  private EquipmentServer server;

  public StopCommand(EquipmentServer s ){
    super( null );
    server = s;
  }

  @Override
  public void exec( List<String> args ){
    try {
      server.stop();
    } catch( Exception e) {
      System.err.println( MessageFormat.format(
        "Failed to stop eqp server. {0}", UtilityHelper.coalesce( e.getMessage(), e ) ) );
    }
  }
}
