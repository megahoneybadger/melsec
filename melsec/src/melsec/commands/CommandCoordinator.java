package melsec.commands;

import melsec.net.Connection;
import melsec.types.io.IOResponse;
import melsec.utils.UtilityHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CommandCoordinator {

  //region Class members
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private Connection connection;
  /**
   *
   */
  private HashMap<String, List<String>> dictGroupToCommands;
  /**
   *
   */
  private HashMap<String, List<CompleteCommand>> dictGroupToResults;
  /**
   *
   */
  private HashMap<String, String> dictCommandsToGroup;
  //endregion

  //region Class properties
  private Logger logger(){
    return LogManager.getLogger();
  }
  //endregion

  //region Class initialization

  /**
   *
   * @param c
   */
  public CommandCoordinator( Connection c ){
    connection = c;
    syncObject = new Object();
    dictGroupToCommands = new HashMap<>();
    dictCommandsToGroup = new HashMap<>();
    dictGroupToResults = new HashMap<>();
  }
  /**
   *
   */
  public void dispose(){
    dictGroupToCommands.clear();
    dictCommandsToGroup.clear();
    dictGroupToResults.clear();
  }
  //endregion

  //region Class public methods

  /**
   *
   * @param commands
   */
  public void group(Iterable<ICommand> commands ){
    try{
      synchronized( syncObject ){
        var group = UUID.randomUUID().toString();

        var ids = UtilityHelper
          .toStream( commands )
          .map( x -> x.id )
          .toList();

        dictGroupToCommands.put( group, ids );
        dictGroupToResults.put( group, new ArrayList<>() );

        ids.forEach( x -> dictCommandsToGroup.put( x, group ) );
      }
    }
    catch( Exception e ){
      logger().error( "Failed to coordinate command completion. {}", e.getMessage() );
    }
  }
  /**
   *
   * @param command
   */
  public void complete(ICommand command, Throwable e ){
    List<CompleteCommand> results = null;
    List<String> order = null;

    synchronized( syncObject ){
      var group = dictCommandsToGroup.get( command.id );
      dictCommandsToGroup.remove( command.id );

      var response = dictGroupToResults.get( group );
      var request = dictGroupToCommands.get( group );

      response.add( new CompleteCommand( command, e ) );

      if( request.size() == response.size() ){
        dictGroupToResults.remove( group );
        dictGroupToCommands.remove( group );
        results = response;
        order = request;
      }
    }

    if( null != results ){
      fire( sort( order, results ) );
    }
  }
  /**
   *
   * @param order
   * @param results
   * @return
   */
  private List<CompleteCommand> sort(List<String> order, List<CompleteCommand> results ){
    var list = new ArrayList<CompleteCommand>();

    for( var o : order ){
      list.add( results
        .stream()
        .filter( x -> x.command.id.equals( o ))
        .findFirst()
        .get());
    }

    return list;
  }
  /**
   *
   * @param list
   */
  private void fire( List<CompleteCommand> list ){
    var response = IOResponse.empty();

    for( var next : list ){
      response = response.merge( next.toResponse() );
    }

    var handler = list
      .stream()
      .map( x -> x.command.unit.handler() )
      .filter( x -> null != x )
      .findFirst();

    if( handler.isPresent() ){
      handler.get().complete( response );
    }
  }
  //endregion

  //region Class internal structs
  record CompleteCommand( ICommand command, Throwable error ){
    public IOResponse toResponse(){
      return command.toResponse( error );
    }
  }
  //endregion
}
