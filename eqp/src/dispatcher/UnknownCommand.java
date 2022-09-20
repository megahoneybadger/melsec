package dispatcher;

import java.util.List;

public class UnknownCommand extends BaseCommand {

  public UnknownCommand(){
    super( null );
  }

  @Override
  public void exec( List<String> args ){
    System.out.println( "Unknown eqp command" );
  }
}
