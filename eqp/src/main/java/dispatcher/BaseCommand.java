package dispatcher;

import melsec.simulation.Memory;

import java.util.List;

public abstract class BaseCommand {
  protected Memory memory;

  public BaseCommand( Memory m ){
    memory = m;
  }

  public abstract void exec( List<String> args );
}
