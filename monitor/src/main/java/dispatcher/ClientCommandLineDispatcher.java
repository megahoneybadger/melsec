package dispatcher;

import dispatcher.multi.MultiReadCommand;
import dispatcher.multi.MultiWriteCommand;
import melsec.EquipmentClient;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientCommandLineDispatcher {

  private final EquipmentClient client;
  private boolean run;

  public ClientCommandLineDispatcher( EquipmentClient c ){
    client = c;

  }

  public void run(){
    run = true;
    var scanner = new Scanner( System.in );

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
        case StartCommand.COMMAND -> new StartCommand( client );
        case StopCommand.COMMAND -> new StopCommand( client );
        case MultiReadCommand.COMMAND -> new MultiReadCommand( client );
        case MultiWriteCommand.COMMAND -> new MultiWriteCommand( client );
        case "quit" -> {
          run = false;
          client.stop();
          client.dispose();
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
