package melsec.net;

import melsec.net.events.IConnectionEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import static melsec.net.Connection.State.*;

public class Connection {

  private final int CONNECTION_TIMEOUT = 5000;

  private String id;

  private final Object syncObject;
  private final Thread processintThread;
  private boolean run;

  private final Endpoint endpoint;
  private AsynchronousSocketChannel socket;
  private State state;
  private Timer timer;

  private ConnectionEvents events;
  //private List<IConnectionEventListener> listeners;

  public Endpoint endpoint(){
    return endpoint;
  }

  public String id(){
    return id;
  }

  public Connection( Endpoint ep ){
    endpoint = ep;
    syncObject = new Object();

    events = new ConnectionEvents( this );

    id = Integer
      .valueOf( new SecureRandom().nextInt( 1000 ))
      .toString();

    state = Disconnected;

    timer = new Timer();

    processintThread = new Thread( () -> processor() );
    processintThread.start();
  }

  public void start(){
    synchronized( syncObject  )
    {
      if( run )
        return;

      run = true;

      scheduleConnect( 100 );
    }
  }

  public void stop(){
    synchronized( syncObject ) {
      if (!run)
        return;

      run = false;

//      if( null != connections ) {
//        connections.dispose();
//        connections = null;
//      }
    }
  }

  public void subscribe( IConnectionEventListener listener ){
    events.add( listener );
  }

  public void unsubscribe( IConnectionEventListener listener ){
    events.remove( listener );
  }

  private void scheduleConnect( int delay ){
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

  private void connect(){
    synchronized ( syncObject ){
      if( !run || state == Connecting )
        return;

      events.connecting();

      state = Connecting;

      try{

        socket = AsynchronousSocketChannel.open();

        var address = new InetSocketAddress( endpoint.address(), endpoint.port() );

        socket.connect(address, socket, new CompletionHandler<>() {
          public void completed(Void result, AsynchronousSocketChannel channel) {
            events.established();

            synchronized ( syncObject ){
              state = Connected;
            }
          }

          public void failed(Throwable exc, AsynchronousSocketChannel channel) {
            synchronized( syncObject ){
              state = Disconnected;
            }

            scheduleConnect( CONNECTION_TIMEOUT );
          }
        });

      }
      catch ( IOException e ){
        connect();
      }
    }
  }

  private void processor(){
    while( true ){

      synchronized ( syncObject ){
        if( !run )
          break;
      }
    }
  }

  enum State {
    Disconnected,
    Connecting,
    Connected
  }
}
