package melsec;

import melsec.types.events.IEventDispatcher;

import melsec.types.io.IORequest;
import melsec.types.events.EventDispatcher;

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

      connection.enqueue( r.toMultiBlockBatchCommands() );
    }
  }
  //endregion
}
