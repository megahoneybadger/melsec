package melsec.net;

import melsec.commands.CommandFactory;
import melsec.commands.ICommand;
import melsec.types.events.IEventDispatcher;

import melsec.types.io.IORequest;
import melsec.types.events.EventDispatcher;
import melsec.utils.UtilityHelper;

import java.util.Dictionary;
import java.util.HashMap;

import static melsec.types.events.EventType.*;

public class EquipmentClient {

  // region Class members
  /**
   *
   */
  private ClientOptions config;
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private boolean run;
  /**
   *
   */
  private Connection connection;
  /**
   *
   */
  private EventDispatcher events;
  /**
   *
   */
  private HashMap<String, Iterable<ICommand>> cache;
  // endregion

  //region Class properties
  /**
   *
   * @return
   */
  public IEventDispatcher events(){
    return events;
  }
  //endregion

  //region Class initializer
  /**
   *
   * @param c
   */
  public EquipmentClient(ClientOptions c ){
    config = c;
    syncObject = new Object();
    events = new EventDispatcher();
    cache = new HashMap<>();
  }
  //endregion

  //region Class public methods
  /**
   *
   */
  public void start(){
    synchronized( syncObject  ) {
      if( run )
        return;

      run = true;
      cache.clear();

      connection = new Connection( config.endpoint(), events );
    }

    events.enqueue(ClientStarted);
  }
  /**
   *
    */
  public void stop(){
    synchronized( syncObject ){
      if( !run )
        return;

      run = false;

      cache.clear();

      if( null != connection ) {
        connection.dispose();
        connection = null;
      }
    }

    events.enqueue(ClientStopped);
  }
  /**
   * @param r
   */
  public void exec( IORequest r ) /*throws DriverNotRunningException*/ {
    if( null == r )
      return;

    synchronized( syncObject ){
//      if( !run )
//        throw new DriverNotRunningException();

      connection.enqueue( toCommands( r ) );
    }
  }
  /**
   *
   * @param r
   * @return
   */
  private Iterable<ICommand> toCommands( IORequest r ){
    var commands = cache.get( r.getId() );

    if( null == commands ){
      // This is optimization step for scanning:
      // avoid splitting the same request
      commands = new CommandFactory().toCommands( r );
      cache.put( r.getId(), commands );
    }

    return commands;
  }
  //endregion
}
