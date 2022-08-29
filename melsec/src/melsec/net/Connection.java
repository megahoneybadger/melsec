package melsec.net;

import melsec.commands.ICommand;
import melsec.events.EventDispatcher;
import melsec.events.net.ConnectionEventArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import static melsec.events.EventType.*;
import static melsec.net.Connection.State.*;

public class Connection {

  //region Class constants
  /**
   *
   */
  private final int CONNECTION_TIMEOUT = 5000;
  //endregion

  //region Class members
  private String id;

  private final Object syncObject;
  private final Thread processingThread;
  private final LinkedList<ICommand> queue;
  private boolean run;

  private final Endpoint endpoint;
  private AsynchronousSocketChannel socket;
  private State state;
  private Timer timer;

  private EventDispatcher events;
  //endregion

  //region Class properties
  public Endpoint endpoint(){
    return endpoint;
  }

  public String id(){
    return id;
  }

  Logger logger(){
    return LogManager.getLogger();
  }

  /**
   *
   * @return
   */
  private ConnectionEventArgs getEventArgs(){
    return new ConnectionEventArgs( endpoint(), id() );
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param ep
   * @param ev
   */
  public Connection( Endpoint ep, EventDispatcher ev ){
    endpoint = ep;
    events = ev;
    syncObject = new Object();
    queue = new LinkedList<>();

    id = Integer
      .valueOf( new SecureRandom().nextInt( 1000 ))
      .toString();

    run = true;
    state = Disconnected;

    timer = new Timer();

    processingThread = new Thread( () -> processor() );
    processingThread.start();

    reconnect( 100 );
  }
  /**
   *
   */
  public void dispose(){
    boolean hadOpenConnection = false;

    synchronized ( syncObject ){
      if( !run )
        return;

      run = false;

      timer.cancel();

      syncObject.notifyAll();

      hadOpenConnection = close();
    }

    try {
      processingThread.join();
    } catch ( InterruptedException e ) {
      logger().error( "failed to stop processing thread" );
    }

    if( hadOpenConnection ){
      events.enqueue( ConnectionDrop, getEventArgs() );
    }

    events.enqueue( ConnectionDisposed, getEventArgs());
  }
  //endregion

  //region Class 'Connection' methods
  /**
   *
   * @param delay
   */
  private void reconnect(int delay ){
    synchronized ( syncObject ){
      if( !run )
        return;

      timer.schedule( new TimerTask() {
        public void run() {
          connect();
        }
      }, delay );
    }
  }
  /**
   *
   */
  private void connect(){
    synchronized ( syncObject ){
      if( !run || state == Connecting )
        return;

      state = Connecting;

      try{
        socket = AsynchronousSocketChannel.open();

        var address = new InetSocketAddress( endpoint.address(), endpoint.port() );

        events.enqueue( ConnectionConnecting, getEventArgs());

        socket.connect(address, socket, new CompletionHandler<>() {
          public void completed(Void result, AsynchronousSocketChannel channel) {
            synchronized ( syncObject ){
              state = Connected;
            }

            events.enqueue( ConnectionEstablished, getEventArgs() );
          }

          public void failed(Throwable exc, AsynchronousSocketChannel channel) {
            synchronized( syncObject ){
              state = Disconnected;
            }

            reconnect( CONNECTION_TIMEOUT );
          }
        });

      }
      catch ( IOException e ){
        reconnect( CONNECTION_TIMEOUT );
      }
    }
  }
  /**
   *
   * @return
   */
  private boolean close(){
    boolean hadOpenConnection = false;

    synchronized( syncObject ){
      hadOpenConnection = ( null != socket && socket.isOpen() );

      if( null == socket )
        return false;

      try {
        socket.close();
      } catch( IOException e ) {
        logger().error("failed to close socket [{}]. {}", id(), e.getMessage());
      }

      socket = null;
    }

    return hadOpenConnection;
  }
  //endregion

  //region Class 'IO' methods
  /**
   *
   * @param command
   */
  public void enqueue( ICommand command ){
    synchronized ( syncObject ){
      if( !run )
        return;

      queue.add( command );
      syncObject.notify();
    }
  }
  /**
   *
   */
  private void processor(){
    while( true ){
      ICommand command = null;

      synchronized ( syncObject ){
        if( !run )
          break;

        if( queue.size() > 0/*&& null == currentPendingCommand*/ ){
          command = queue.remove();
        } else {
          try {
            syncObject.wait();
          } catch (InterruptedException e) {
            logger().error( "failed to put connection#{} process to sleep", id() );
          }
        }
      }

      if( null != command ){
        send( command );
      }
    }

    logger().debug( "connection#{} processor stopped", id() );
  }

  /**
   *
   * @param command
   */
  private void send( ICommand command ){

  }
  //endregion

  //region Class internal structs
  enum State {
    Disconnected,
    Connecting,
    Connected
  }
  //endregion
}
