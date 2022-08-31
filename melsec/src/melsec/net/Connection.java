package melsec.net;

import melsec.io.commands.Coder;
import melsec.io.commands.ICommand;
import melsec.events.EventDispatcher;
import melsec.events.net.ConnectionEventArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
  private ICommand currentPendingCommand;

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
  /**
   *
   * @param c
   * @param e
   */
  private void drop( ICommand c, Throwable e ){
//    done( c, new RtException( RtException.Code.SocketGeneralError, e.toString(), id ) );
//
//    if( close() ){
//      events.enqueue( new ConnectionEventArgs( endpoint, id, ConnectionEventArgs.Type.Dropped ) );
//    }
//
//    connect();
  }
  //endregion

  //region Class 'IO' methods
  /**
   *
   * @param commands
   */
  public void enqueue( List<ICommand> commands ){
    synchronized ( syncObject ){
      if( !run )
        return;

      queue.addAll( commands );
      syncObject.notify();
    }
  }
  /**
   *
   */
  private void processor(){
    while( true ){
      synchronized ( syncObject ){
        if( !run )
          break;

        if( queue.size() > 0 && null == currentPendingCommand ){
          send( queue.remove() );
        } else {
          try {
            syncObject.wait();
          } catch (InterruptedException e) {
            logger().error( "failed to put connection#{} processor to sleep", id() );
          }
        }
      }
    }

    logger().debug( "connection#{} processor stopped", id() );
  }
  /**
   *
   * @param command
   */
  private void send( ICommand command ){
    try {
      currentPendingCommand = command;

      var buffer = ByteBuffer.wrap( command.encode() );
      var arr = buffer.array();

      socket.write( buffer, command, new CompletionHandler<>() {
        @Override
        public void completed( Integer count, ICommand command ) {
          recvHeader( command );
        }

        @Override
        public void failed( Throwable e, ICommand command ) {
          drop( command, e );
        }
      } );
    }
    catch( Exception e ) {
      logger().error( e.getMessage() );
      //command.complete
    }
  }
  /**
   *
   * @param command
   */
  private void recvHeader( ICommand command ){
    try {
      var buffer = ByteBuffer.allocate( Coder.HEADER_LENGTH );

      synchronized( syncObject ) {
        socket.read( buffer, 1, TimeUnit.SECONDS, command, new CompletionHandler<>() {
          @Override
          public void completed( Integer countReadBytes, ICommand command ) {
            if( countReadBytes < Coder.HEADER_LENGTH ){
              done( command, new Exception( "failed to read header" ) );
            } else {
              var replyBodySize = Coder.decodeLength( buffer.array() );
              var replyTotalSize = Coder.HEADER_LENGTH + replyBodySize;

              var bufferTotal = ByteBuffer.allocate( replyTotalSize );
              System.arraycopy( buffer.array(), 0, bufferTotal.array(), 0, Coder.HEADER_LENGTH );

              bufferTotal.position( Coder.HEADER_LENGTH );

              recvBody( new BodyRecvProgress( command, bufferTotal, replyBodySize ) );
            }
          }

          @Override
          public void failed( Throwable e, ICommand command ) {
            //drop( c, e );
          }
        } );
      }
    }
    catch( Exception e ){
      drop( command, e );
    }
  }
  /**
   *
   * @param p
   */
  private void recvBody( BodyRecvProgress p ){
    try{
      synchronized( syncObject ){
        socket.read( p.buffer, 1, TimeUnit.SECONDS, p, new CompletionHandler<>() {
          @Override
          public void completed( Integer readFromStreamCount, BodyRecvProgress pars ) {
            var left = pars.bytesToReadLeft - readFromStreamCount;

            if( left > 0 ) {
              recvBody( new BodyRecvProgress( p.command, p.buffer, left ));
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
      //drop
    }
  }
  /**
   *
    * @param p
   */
  private void decode( BodyRecvProgress p ){
    try{
      p.command.decode( p.buffer.array() );

      done( p.command, null );
    }
    catch( Exception exc ){
      //done( c, new RtException( RtException.Code.CommandDecodingError, exc.toString() ) );
    }
  }
  /**
   *
   * @param c
   * @param e
   */
  private void done( ICommand c, Exception e ){

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

