package melsec.events;

import melsec.events.driver.IDriverStartedEvent;
import melsec.events.driver.IDriverStoppedEvent;
import melsec.events.net.IConnectionDisposedEvent;
import melsec.events.net.IConnectionEstablishedEvent;
import melsec.events.net.IConnectionConnectingEvent;

public interface IEventDispatcher {
  void subscribe( IDriverStartedEvent h );
  void unsubscribe( IDriverStartedEvent h );

  void subscribe( IDriverStoppedEvent h );
  void unsubscribe( IDriverStoppedEvent h );

  void subscribe( IConnectionConnectingEvent h );
  void unsubscribe( IConnectionConnectingEvent h );

  void subscribe( IConnectionEstablishedEvent h );
  void unsubscribe( IConnectionEstablishedEvent h );

  void subscribe( IConnectionDisposedEvent h );
  void unsubscribe( IConnectionDisposedEvent h );
}