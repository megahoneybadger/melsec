package melsec.events;

import melsec.events.driver.IDriverStartedEvent;
import melsec.events.driver.IDriverStoppedEvent;
import melsec.events.net.ConnectionEventArgs;
import melsec.events.net.IConnectionEstablishedEvent;
import melsec.events.net.IConnectionConnectingEvent;
import melsec.events.net.IConnectionDisposedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;


public final class EventDispatcher implements IEventDispatcher  {

  private EventBox driverStarted = new EventBox();
  private EventBox driverStopped = new EventBox();

  private EventBox<ConnectionEventArgs> channelConnecting = new EventBox();
  private EventBox<ConnectionEventArgs> channelConnected = new EventBox();
  private EventBox<ConnectionEventArgs> channelDisposed = new EventBox();

  private Thread thread;
  private Object syncObject;
  private boolean run;

  private LinkedList<Message> queue;

  Logger logger(){
    return LogManager.getLogger( "driver" );
  }

  public EventDispatcher(){
    syncObject = new Object();
    queue = new LinkedList<>();
    run = true;

    thread = new Thread( () -> processor() );
    thread.start();
  }

  public void subscribe( IDriverStartedEvent handler ){
    synchronized( syncObject ){
      driverStarted.add( handler );
    }
  }

  public void unsubscribe( IDriverStartedEvent handler ){
    synchronized( syncObject ){
      driverStarted.remove( handler );
    }
  }

  public void subscribe( IDriverStoppedEvent handler ){
    synchronized( syncObject ) {
      driverStopped.add(handler);
    }
  }

  public void unsubscribe( IDriverStoppedEvent handler ){
    synchronized( syncObject ) {
      driverStopped.remove(handler);
    }
  }

  public void subscribe( IConnectionConnectingEvent handler ){
    synchronized( syncObject ) {
      channelConnecting.add(handler);
    }
  }

  public void unsubscribe( IConnectionConnectingEvent handler ){
    synchronized( syncObject ) {
      channelConnecting.remove(handler);
    }
  }

  public void subscribe( IConnectionEstablishedEvent handler ){
    synchronized( syncObject ) {
      channelConnected.add(handler);
    }
  }

  public void unsubscribe( IConnectionEstablishedEvent handler ){
    synchronized( syncObject ) {
      channelConnected.remove(handler);
    }
  }

  public void subscribe( IConnectionDisposedEvent handler ){
    synchronized( syncObject ) {
      channelDisposed.add(handler);
    }
  }

  public void unsubscribe( IConnectionDisposedEvent handler ){
    synchronized( syncObject ) {
      channelDisposed.remove(handler);
    }
  }

  public void enqueue( EventType type ){
    enqueue( type, null );
  }

  public void enqueue( EventType type, IEventArgs args ){
    synchronized ( syncObject ){
      if( !run )
        return;

      queue.add( new Message( type, args ) );
      syncObject.notify();
    }
  }

  private void processor(){
    while( true ){

      Message message = null;

      synchronized ( syncObject ){
        if( !run )
          break;

        if( queue.size() > 0 ){
          message = queue.remove();
        } else {
          try {
            syncObject.wait();
          } catch ( InterruptedException e ) {
          }
        }
      }

      if( null != message ){
        fire( message );
      }
    }
  }

  private void fire( Message message ){


    switch ( message.type() ){
      case DriverStarted -> {
        logger().trace( "driver started" );
        driverStarted.fire();
      }
      case DriverStopped -> {
        logger().trace( "driver stopped" );
        driverStopped.fire();
      }
      case ConnectionConnecting -> channelConnecting.fire( ( ConnectionEventArgs ) message.args() );
      case ConnectionDisposed -> channelDisposed.fire( ( ConnectionEventArgs ) message.args() );
    }
  }

  record Message(EventType type, IEventArgs args ) {
  }
}
