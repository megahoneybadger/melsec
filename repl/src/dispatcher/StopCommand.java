package dispatcher;

import melsec.Driver;

import java.util.List;

public class StopCommand extends BaseCommand {
  public final static String COMMAND = "stop";

  public StopCommand( Driver c ){
    super( c );
  }

  @Override
  public void exec( List<String> args ){
    communicator.stop();
  }
}
