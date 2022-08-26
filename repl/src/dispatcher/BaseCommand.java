package dispatcher;

import melsec.Driver;

import java.util.List;

public abstract class BaseCommand {
  protected Driver communicator;

  public BaseCommand( Driver c ){
    communicator = c;
  }

  public abstract void exec( List<String> args );
}
