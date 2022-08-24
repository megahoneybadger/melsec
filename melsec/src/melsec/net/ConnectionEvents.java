package melsec.net;

import melsec.net.events.ConnectionEventArgs;
import melsec.net.events.IConnectionEventListener;

import java.util.ArrayList;
import java.util.List;

class ConnectionEvents {

  private Connection connection;
  private List<IConnectionEventListener> listeners;

  private ConnectionEventArgs args(){
    return new ConnectionEventArgs( connection.endpoint(), connection.id() );
  }

  public ConnectionEvents( Connection c ){
    connection = c;
    listeners = new ArrayList<>();
  }

  public void add( IConnectionEventListener l ){
    listeners.add( l );
  }

  public void remove( IConnectionEventListener l ){
    listeners.remove( l );
  }

  //  add
  public void connecting() {
    listeners.forEach( x -> x.connecting( args() ) );
  }

  public void established(){
    listeners.forEach( x -> x.established(args()) );
  }

  public void dropped(){
    listeners.forEach( x -> x.dropped( args() ) );
  }

  public void disposed(){
    listeners.forEach( x -> x.disposed( args() ) );
  }



}
