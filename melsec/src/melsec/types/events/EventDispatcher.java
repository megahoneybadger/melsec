package melsec.types.events;

import melsec.types.events.client.IClientStartedEvent;
import melsec.types.events.client.IClientStoppedEvent;
import melsec.types.events.net.*;
import melsec.types.events.scanner.IScannerChangeEvent;
import melsec.types.events.scanner.ScannerEventArgs;
import melsec.types.log.LogLevel;
import melsec.utils.Stringer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;


public final class EventDispatcher implements IEventDispatcher  {

  //region Class members
  /**
   *
   */
  private EventBox driverStarted = new EventBox();
  /**
   *
   */
  private EventBox driverStopped = new EventBox();

  /**
   *
   */
  private EventBox<ConnectionEventArgs> conConnecting = new EventBox();
  /**
   *
   */
  private EventBox<ConnectionEventArgs> conConnected = new EventBox();
  /**
   *
   */
  private EventBox<ConnectionEventArgs> conDropped = new EventBox();
  /**
   *
   */
  private EventBox<ConnectionEventArgs> conDisposed = new EventBox();
  /**
   *
   */
  private EventBox<ScannerEventArgs> scannerChange = new EventBox();
  /**
   *
   */
  private Thread thread;
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
  private LinkedList<Message> queue;
  //endregion

  //region Class properties
  Logger logger(){
    return LogManager.getLogger();
  }
  //endregion

  //region Class initialization
  /**
   *
   */
  public EventDispatcher(){
    syncObject = new Object();
    queue = new LinkedList<>();
    run = true;

    thread = new Thread( () -> processor() );
    thread.start();
  }
  //endregion

  //region Class 'Subscription' methods
  public void subscribe( IClientStartedEvent handler ){
    synchronized( syncObject ){
      driverStarted.add( handler );
    }
  }

  public void unsubscribe( IClientStartedEvent handler ){
    synchronized( syncObject ){
      driverStarted.remove( handler );
    }
  }

  public void subscribe( IClientStoppedEvent handler ){
    synchronized( syncObject ) {
      driverStopped.add(handler);
    }
  }

  public void unsubscribe( IClientStoppedEvent handler ){
    synchronized( syncObject ) {
      driverStopped.remove(handler);
    }
  }

  public void subscribe( IConnectionConnectingEvent handler ){
    synchronized( syncObject ) {
      conConnecting.add(handler);
    }
  }

  public void unsubscribe( IConnectionConnectingEvent handler ){
    synchronized( syncObject ) {
      conConnecting.remove(handler);
    }
  }

  public void subscribe( IConnectionEstablishedEvent handler ){
    synchronized( syncObject ) {
      conConnected.add(handler);
    }
  }

  public void unsubscribe( IConnectionEstablishedEvent handler ){
    synchronized( syncObject ) {
      conConnected.remove(handler);
    }
  }

  public void subscribe( IConnectionDisposedEvent handler ){
    synchronized( syncObject ) {
      conDisposed.add(handler);
    }
  }

  public void unsubscribe( IConnectionDisposedEvent handler ){
    synchronized( syncObject ) {
      conDisposed.remove(handler);
    }
  }

  public void subscribe( IConnectionDroppedEvent handler ){
    synchronized( syncObject ) {
      conDropped.add(handler);
    }
  }

  public void unsubscribe( IConnectionDroppedEvent handler ){
    synchronized( syncObject ) {
      conDropped.remove(handler);
    }
  }

  public void subscribe( IScannerChangeEvent handler ){
    synchronized( syncObject ) {
      scannerChange.add(handler);
    }
  }

  public void unsubscribe( IScannerChangeEvent handler ){
    synchronized( syncObject ) {
      scannerChange.remove(handler);
    }
  }

  //endregion

  //region Class 'Processor' methods
  /**
   *
   * @param type
   */
  public void enqueue( EventType type ){
    enqueue( type, ( IEventArgs )null );
  }
  /**
   *
   * @param type
   * @param args
   */
  public void enqueue( EventType type, IEventArgs args ){
    synchronized ( syncObject ){
      if( !run )
        return;

      queue.add( new Message( type, args ) );
      syncObject.notify();
    }
  }
  /**
   *
   */
  private void processor() {
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
  /**
   *
   * @param message
   */
  private void fire( Message message )  {
    var args = message.args();

    switch ( message.type() ){
      case ClientStarted -> {
        logger().info( "Client started" );
        driverStarted.fire();
      }
      case ClientStopped -> {
        logger().info( "Client stopped" );
        driverStopped.fire();
      }
      case ConnectionConnecting ->{
        logger().debug( "Connection#{} trying to connect to {}",
          (( ConnectionEventArgs )args ).id(), args );
        conConnecting.fire( ( ConnectionEventArgs ) args );
      }
      case ConnectionEstablished ->{
        logger().info( "Connection#{} established",
          (( ConnectionEventArgs )args ).id() );
        conConnected.fire( ( ConnectionEventArgs ) args );
      }
      case ConnectionDropped ->{
        logger().info( "Connection#{} dropped",
          (( ConnectionEventArgs )args ).id() );
        conDropped.fire( ( ConnectionEventArgs ) args );
      }

      case ScannerChanges ->{
        var level = Level.getLevel( String.valueOf( LogLevel.SCAN ) );
        var changes = (( ScannerEventArgs ) args ).changes();

        changes.forEach( x -> logger().log( level, Stringer.toString( x, true )));

        scannerChange.fire( ( ScannerEventArgs ) args );
      }
    }
  }
  //endregion

  //region Class internal structs
  record Message(EventType type, IEventArgs args ) {}
  //endregion
}
