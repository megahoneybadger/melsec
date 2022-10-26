package dispatcher;

import dispatcher.multi.MultiReadCommand;
import dispatcher.multi.MultiWriteCommand;
import melsec.net.EquipmentClient;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientCommandLineDispatcher {

  private final EquipmentClient communicator;
  private boolean run;

  public ClientCommandLineDispatcher( EquipmentClient c ){
    communicator = c;

  }

  public void run(){
    run = true;
    var scanner = new Scanner( System.in );

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
        case StartCommand.COMMAND -> new StartCommand( communicator );
        case StopCommand.COMMAND -> new StopCommand( communicator );
        case MultiReadCommand.COMMAND -> new MultiReadCommand( communicator );
        case MultiWriteCommand.COMMAND -> new MultiWriteCommand( communicator );
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
