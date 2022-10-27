package dispatcher;

import dispatcher.io.ReadCommand;
import dispatcher.io.WriteCommand;
import melsec.simulation.Memory;
import melsec.simulation.ServerOptions;
import melsec.simulation.EquipmentServer;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class EquipmentCommandLineDispatcher implements Runnable {

  //region Class members
  /**
   *
   */
  private final Memory memory;
  /**
   *
   */
  private boolean run;
  /**
   *
   */
  private EquipmentServer server;
  //endregion

  //region Class initialization
  /**
   *
   * @param options
   */
  public EquipmentCommandLineDispatcher( ServerOptions options ){
    memory = options.memory();

    server = new EquipmentServer( options );
  }
  //endregion

  //region Class public commands
  /**
   *
   */
  public void run(){
    run = true;
    var scanner = new Scanner( System.in );

    new StartCommand( server ).exec( null );

    while( run && scanner.hasNextLine() ){
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
        case ReadCommand.COMMAND -> new ReadCommand( memory );
        case WriteCommand.COMMAND -> new WriteCommand( memory );
        case StartCommand.COMMAND -> new StartCommand( server );
        case StopCommand.COMMAND -> new StopCommand( server );
        case ResetCommand.COMMAND -> new ResetCommand( memory );
        case "quit" -> {
          run = false;
          yield null;

        }
        default -> new UnknownCommand();
      };

      exec( res, args );
    }
  }
  /**
   *
   * @param command
   * @param args
   */
  private void exec( BaseCommand command, List<String> args ){
    if( null == command )
      return;

    try{
      command.exec( args );
    }
    catch( Exception e ){
      System.err.println( MessageFormat
        .format( "Failed to execute command. {0}", e.getMessage() ) );
    }
  }
  //endregion
}
