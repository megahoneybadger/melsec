package dispatcher;

import melsec.EquipmentClient;

import java.util.List;

public class StartCommand extends BaseCommand  {
  public final static String COMMAND = "start";

  public StartCommand( EquipmentClient c ){
    super( c );
  }

  @Override
  public void exec( List<String> args ){
    communicator.start();
  }
}
