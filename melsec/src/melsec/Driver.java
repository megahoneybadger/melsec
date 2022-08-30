package melsec;

import melsec.events.IEventDispatcher;
import melsec.io.IORequest;
import melsec.net.Connection;
import melsec.events.EventDispatcher;

import static melsec.events.EventType.*;

public class Driver {

  // region Class members
  /**
   *
   */
  private Config config;
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
  public Driver( Config c ){
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

    events.enqueue( DriverStarted );
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

    events.enqueue( DriverStopped );
  }
  /**
   * @param r
   */
  public void exec( IORequest r ) {
    synchronized( syncObject ){
      if( !run )
        return;

      connection.enqueue( r.toCommands() );
    }
  }
  //endregion
}
