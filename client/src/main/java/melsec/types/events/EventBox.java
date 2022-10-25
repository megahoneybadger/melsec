package melsec.types.events;

import java.util.ArrayList;

public class EventBox<T extends IEventArgs> {
  private ArrayList<IEvent> listeners;

  public EventBox(){
    listeners = new ArrayList<>();
  }

  public void add( IEvent l ){
    listeners.add( l );
  }

  public void remove( IEvent l ){
    listeners.remove( l );
  }

  public void fire( T args ) {
    for( var l :listeners  ){
      l.executed( args );
    }
  }

  public void fire()  {
    fire( null );
  }
}
