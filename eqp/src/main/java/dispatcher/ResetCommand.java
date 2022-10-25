package dispatcher;

import melsec.simulation.Memory;

import java.util.List;

public class ResetCommand extends BaseCommand  {
  public final static String COMMAND = "reset";

  public ResetCommand( Memory m){
    super( m );
  }

  @Override
  public void exec( List<String> args ){
    memory.reset();
  }
}
