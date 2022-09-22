package dispatcher;

import melsec.EquipmentClient;

import java.util.List;

public class StopCommand extends BaseCommand {
  public final static String COMMAND = "stop";

  public StopCommand( EquipmentClient c ){
    super( c );
  }

  @Override
  public void exec( List<String> args ){
    communicator.stop();
  }
}
