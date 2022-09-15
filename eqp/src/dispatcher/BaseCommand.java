package dispatcher;

import melsec.simulation.Equipment;

import java.util.List;

public abstract class BaseCommand {
  protected Equipment eqp;

  public BaseCommand( Equipment e ){
    eqp = e;
  }

  public abstract void exec( List<String> args );
}
