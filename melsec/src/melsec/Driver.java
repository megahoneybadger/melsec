package melsec;

import melsec.config.PlcConfig;
import melsec.events.IEventDispatcher;
import melsec.io.IOHandler;
import melsec.io.IORequest;
import melsec.net.Connection;
import melsec.events.EventDispatcher;

import static melsec.events.EventType.*;

public class Driver {

  private PlcConfig config;
  private Object syncObject;
  private boolean run;
  private Connection connection;
  private EventDispatcher events;

  public IEventDispatcher events(){
    return events;
  }

  public Driver(PlcConfig c ){
    config = c;
    syncObject = new Object();
    events = new EventDispatcher();
  }

  public void start(){
    synchronized( syncObject  )
    {
      if( run )
        return;

      run = true;

      connection = new Connection( config.endpoint(), events );
    }

    events.enqueue( DriverStarted );

  }

  public void stop(){
    synchronized( syncObject )
    {
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

  public void exec( IORequest r, IOHandler handler) {
  }
}
