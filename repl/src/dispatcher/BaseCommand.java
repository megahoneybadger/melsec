package dispatcher;

import melsec.net.EquipmentClient;

import java.util.List;

public abstract class BaseCommand {
  protected EquipmentClient communicator;

  public BaseCommand( EquipmentClient c ){
    communicator = c;
  }

  public abstract void exec( List<String> args );
}
