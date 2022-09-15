package dispatcher;

import dispatcher.io.ReadCommand;
import dispatcher.io.WriteCommand;
import melsec.simulation.Equipment;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineDispatcher {

  private final Equipment eqp;
  private boolean run;

  public CommandLineDispatcher(){
    this( new Equipment() );
  }

  public CommandLineDispatcher( Equipment e ){
    eqp = e;
  }

  public void run(){
    run = true;
    var scanner = new Scanner( System.in );
    System.out.println( "eqp is running" );

    while( run ){
      var line = scanner
        .nextLine()
        .trim();

      var parts = line.split( " " );

      if( 0 == parts.length )
        continue;

      var command = parts[ 0 ];

      var args = Arrays
        .stream( parts )
        .filter( x -> !x.isEmpty() && !x.isBlank() )
        .skip( 1 )
        .toList();

      var res = switch ( command.toLowerCase() )
      {
//        case StartCommand.COMMAND -> new StartCommand( communicator );
//        case StopCommand.COMMAND -> new StopCommand( communicator );
        case ReadCommand.COMMAND -> new ReadCommand( eqp );
        case WriteCommand.COMMAND -> new WriteCommand( eqp );
        case "quit" -> {
          run = false;
          yield null;

        }
        default -> new UnknownCommand();
      };

      exec( res, args );
    }
  }

  private void exec( BaseCommand command, List<String> args ){
    if( null == command )
      return;

    try{
      command.exec( args );
    }
    catch( Exception e ){

    }
  }
}
