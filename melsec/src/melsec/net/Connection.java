package melsec.net;

import melsec.commands.CommandCoordinator;
import melsec.types.exceptions.ConnectionNotEstablishedException;
import melsec.commands.ICommand;
import melsec.types.events.EventDispatcher;
import melsec.types.events.net.ConnectionEventArgs;
import melsec.types.Endpoint;
import melsec.types.log.LogLevel;
import melsec.utils.Coder;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static melsec.types.events.EventType.*;
import static melsec.net.Connection.State.*;

public class Connection {

  //region Class constants
  /**
   *
   */
  private final int CONNECTION_LONG_TIMEOUT = 5000;
  /**
   *
   */
  private final int CONNECTION_SHORT_TIMEOUT = 100;
  //endregion

  //region Class members
  private String id;

  private final Object syncObject;
  private final Thread processingThread;
  private final LinkedList<ICommand> queue;
  private boolean run;
  private ICommand currentPendingCommand;

  private final Endpoint endpoint;
  private AsynchronousSocketChannel socket;
  private State state;
  private Timer timer;

  private EventDispatcher events;
  private CommandCoordinator coordinator;

  private Logger logger;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public Endpoint endpoint(){
    return endpoint;
  }
  /**
   *
   * @return
   */
  public String id(){
    return id;
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
    coordinator = new CommandCoordinator( this );

    run = true;
    state = Disconnected;

    timer = new Timer();

    processingThread = new Thread( () -> processor() );
    processingThread.start();

    logger = LogManager.getLogger();

    reconnect( CONNECTION_SHORT_TIMEOUT, true );
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
      coordinator.dispose();

      syncObject.notifyAll();

      hadOpenConnection = close();
    }

    try {
      processingThread.join();
    } catch ( InterruptedException e ) {
      logger.error( "failed to stop processing thread" );
    }

    if( hadOpenConnection ){
      events.enqueue(ConnectionDropped, getEventArgs() );
    }

    events.enqueue( ConnectionDisposed, getEventArgs());
  }
  //endregion

  //region Class 'Connection' methods
  /**
  /**
   *
   * @param delay
   */
  private void reconnect( int delay, boolean renewId ){
    synchronized ( syncObject ){
      if( !run )
        return;

      if( renewId ){
        id = Integer
          .valueOf( new SecureRandom().nextInt( 1000 ))
          .toString();
      }

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
          public void completed( Void result, AsynchronousSocketChannel channel) {
            synchronized ( syncObject ){
              state = Connected;
            }

            events.enqueue( ConnectionEstablished, getEventArgs() );
          }

          public void failed(Throwable exc, AsynchronousSocketChannel channel) {
            synchronized( syncObject ){
              state = Disconnected;
            }

            reconnect(CONNECTION_LONG_TIMEOUT, false );
          }
        });
      }
      catch ( IOException e ){
        reconnect(CONNECTION_LONG_TIMEOUT, false );
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

      state = Disconnected;

      if( null == socket )
        return false;

      try {
        socket.close();
      } catch( IOException e ) {
        logger.error("Failed to close socket [{}]. {}", id(), e.getMessage());
      }

      socket = null;
    }

    return hadOpenConnection;
  }

  //endregion

  //region Class 'IO' methods
  /**
   *
   * @param commands
   */
  public void enqueue( Iterable<ICommand> commands ){
    if( null == commands )
      return;

    synchronized ( syncObject ){
      if( !run )
        return;

      logDebug( "Enqueue {}", () -> Stringer.toString( commands ) );

      coordinator.group( commands );

      queue.addAll( UtilityHelper.toList( commands ) );
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

        if( queue.size() > 0 && null == currentPendingCommand ){
          command = queue.remove();
        } else {
          try {
            syncObject.wait();
          } catch (InterruptedException e) {
            logger.error( "Failed to put connection#{} processor to sleep", id() );
          }
        }
      }

      if( null != command ){
        sendAsync( command );
      }
    }

    logger.debug( "Connection#{} processor stopped", id() );
  }
  /**
   *
   * @param command
   */
  private void sendAsync( ICommand command ){
    try {
      synchronized( syncObject ){
        currentPendingCommand = command;

        logDebug( "Process {}", () -> command.toString() );

        if( state != Connected )
          throw new ConnectionNotEstablishedException( endpoint );

        var buffer = ByteBuffer.wrap( command.encode() );

        socket.write( buffer, command, new CompletionHandler<>() {
          @Override
          public void completed( Integer count, ICommand command ) {
            logNet( "After send {}", command );
            recvHeaderAsync( command );
          }

          @Override
          public void failed( Throwable e, ICommand command ) {
            drop( command, e );
          }
        } );
      }
    }
    catch( Exception e ){
      done( command, e );
    }
  }
  /**
   *
   * @param command
   */
  private void recvHeaderAsync(ICommand command ){
    try {
      var buffer = ByteBuffer.allocate( Coder.HEADER_LENGTH );
      logNet( "Receiving {} header", command );

      synchronized( syncObject ) {
        socket.read( buffer, 1, TimeUnit.SECONDS, command, new CompletionHandler<>() {
          @Override
          public void completed( Integer countReadBytes, ICommand command ) {
            if( countReadBytes < Coder.HEADER_LENGTH ){
              done( command, new Exception( "Failed to read header" ) );
            } else {
              var replyBodySize = Coder.getCommandBodySize( buffer.array() );
              var replyTotalSize = Coder.HEADER_LENGTH + replyBodySize;

              logNet( "Received {} header: expecting {} bytes in body", command, replyBodySize );

              var bufferTotal = ByteBuffer.allocate( replyTotalSize );
              System.arraycopy( buffer.array(), 0, bufferTotal.array(), 0, Coder.HEADER_LENGTH );

              bufferTotal.position( Coder.HEADER_LENGTH );

              recvBodyAsync( new BodyRecvProgress( command, bufferTotal, replyBodySize ) );
            }
          }

          @Override
          public void failed( Throwable e, ICommand command ) {
            drop( command, e );
          }
        } );
      }
    }
    catch( Exception e ){
      done( command, e );
    }
  }
  /**
   *
   * @param p
   */
  private void recvBodyAsync( BodyRecvProgress p ){
    try{
      logNet( "Receiving {} body: {} bytes left", p.command, p.bytesToReadLeft );

      synchronized( syncObject ){
        socket.read( p.buffer, 1, TimeUnit.SECONDS, p, new CompletionHandler<>() {
          @Override
          public void completed( Integer readFromStreamCount, BodyRecvProgress pars ) {
            var left = pars.bytesToReadLeft - readFromStreamCount;

            if( left > 0 ) {
              recvBodyAsync( new BodyRecvProgress( p.command, p.buffer, left ));
            } else {
              decode( p );
            }
          }

          @Override
          public void failed( Throwable e, BodyRecvProgress pars ) {
            drop( p.command, e );
          }
        });
      }
    }
    catch( Exception e ){
      drop( p.command, e );
    }
  }
  /**
   *
    * @param p
   */
  private void decode( BodyRecvProgress p ){
    try{
      logNet( "Received {} body", p.command );

      p.command.decode( p.buffer.array() );

      logDebug( "Complete {}", () -> p.command.toString() );

      done( p.command, null );
    }
    catch( Exception exc ){
      done( p.command, exc );
    }
  }
  /**
   *
   * @param c
   * @param e
   */
  private void done( ICommand c, Throwable e ){
    synchronized( syncObject ){
      currentPendingCommand = null;
      syncObject.notify();
    }

    if( null != e ){
      logger.error( "Failed to complete {}. {}", c,
        UtilityHelper.coalesce( e.getMessage(), e.toString() ) );
    }

    coordinator.complete( c, e );
    //c.complete( e );
  }
  /**
   *
   * @param c
   * @param e
   */
  private void drop( ICommand c, Throwable e ){
    done( c, e );

    if( close() ){
      events.enqueue( ConnectionDropped, getEventArgs() );
    }

    reconnect( CONNECTION_SHORT_TIMEOUT, true );
  }
  //endregion

  //region Class 'Log' methods

  /**
   *
   * @param message
   */
  private void logNet( String message, Object...args ){
    var verb = String.valueOf( LogLevel.NET );
    var level = Level.getLevel( verb );
    logger.log( level, message, args );
  }
  /**
   *
   * @param message
   * @param func
   */
  private void logDebug( String message, Supplier<String > func ){
    if( Level.DEBUG.compareTo( logger.getLevel() ) != 1 ){
      logger.debug( message, func.get() );
    }
  }

  //endregion

  //region Class internal structs
  enum State {
    Disconnected,
    Connecting,
    Connected
  }

  record BodyRecvProgress( ICommand command,
                           ByteBuffer buffer,
                           int bytesToReadLeft ){}
  //endregion
}

