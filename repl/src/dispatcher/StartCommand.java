package dispatcher;

import melsec.Driver;

import java.util.List;

public class StartCommand extends BaseCommand  {
  public final static String COMMAND = "start";

  public StartCommand( Driver c ){
    super( c );
  }

  @Override
  public void exec( List<String> args ){
    communicator.start();
  }
}
