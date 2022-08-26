package melsec.net;

import melsec.events.EventDispatcher;
import melsec.events.EventType;
import melsec.events.net.ConnectionEventArgs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import static melsec.events.EventType.*;
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

  private EventDispatcher events;


  public Endpoint endpoint(){
    return endpoint;
  }

  public String id(){
    return id;
  }

  public Connection(Endpoint ep, EventDispatcher ev ){
    endpoint = ep;
    events = ev;
    syncObject = new Object();

    id = Integer
      .valueOf( new SecureRandom().nextInt( 1000 ))
      .toString();

    run = true;
    state = Disconnected;

    timer = new Timer();

    processintThread = new Thread( () -> processor() );
    processintThread.start();

    scheduleConnect( 100 );
  }

  public void dispose(){
    //boolean hadOpenConnection = false;

    synchronized ( syncObject ){
      if( !run )
        return;

      run = false;

      timer.cancel();

      syncObject.notifyAll();
    }

    events.enqueue( ConnectionDisposed,
      new ConnectionEventArgs( endpoint(), id() ));
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

      state = Connecting;

      try{

        socket = AsynchronousSocketChannel.open();

        var address = new InetSocketAddress( endpoint.address(), endpoint.port() );

        socket.connect(address, socket, new CompletionHandler<>() {
          public void completed(Void result, AsynchronousSocketChannel channel) {
            //events.established();

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

    events.enqueue( ConnectionConnecting,
      new ConnectionEventArgs( endpoint(), id() ));
  }

  private void processor(){
    while( true ){

      synchronized ( syncObject ){
        if( !run )
          break;
      }
    }

    System.out.println( "connection processor stopped" );
  }

  enum State {
    Disconnected,
    Connecting,
    Connected
  }
}
