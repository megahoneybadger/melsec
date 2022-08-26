package dispatcher;

import melsec.Driver;
import melsec.events.net.IConnectionConnectingEvent;
import melsec.events.driver.IDriverStartedEvent;
import melsec.events.driver.IDriverStoppedEvent;
import melsec.events.net.IConnectionDisposedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineDispatcher {

  private Driver communicator;
  private boolean run;

  public CommandLineDispatcher( Driver c ){
    communicator = c;

    c.events().subscribe( ( IDriverStartedEvent ) args ->
      System.out.println( "driver started" ));

    c.events().subscribe( (IDriverStoppedEvent ) args ->
      System.out.println( "driver stopped" ));

    c.events().subscribe( ( IConnectionConnectingEvent) args ->
      System.out.println( "channel connecting" ));

    c.events().subscribe( (IConnectionDisposedEvent ) args ->
      System.out.println( "connection disposed" ));

  }

  public void run(){
    run = true;
    var scanner = new Scanner( System.in );

    while( run ){
      var line = scanner
        .nextLine()
        .trim()
        .toLowerCase();

      var parts = line.split( " " );

      if( null == parts || 0 == parts.length )
        continue;

      var command = parts[ 0 ];

      var args = Arrays
        .stream( parts )
        .skip( 1 )
        .toList();

      var res = switch ( command )
      {
        case StartCommand.COMMAND -> new StartCommand( communicator );
        case StopCommand.COMMAND -> new StopCommand( communicator );
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
