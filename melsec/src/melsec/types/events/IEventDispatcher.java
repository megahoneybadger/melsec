package melsec.types.events;

import melsec.types.events.client.IClientStartedEvent;
import melsec.types.events.client.IClientStoppedEvent;
import melsec.types.events.net.IConnectionConnectingEvent;
import melsec.types.events.net.IConnectionDisposedEvent;
import melsec.types.events.net.IConnectionEstablishedEvent;
import melsec.types.events.scanner.IScannerChangeEvent;

public interface IEventDispatcher {

  void enqueue( EventType type );
  void enqueue( EventType type, IEventArgs args );

  void subscribe( IClientStartedEvent h );
  void unsubscribe( IClientStartedEvent h );

  void subscribe( IClientStoppedEvent h );
  void unsubscribe( IClientStoppedEvent h );

  void subscribe( IConnectionConnectingEvent h );
  void unsubscribe( IConnectionConnectingEvent h );

  void subscribe( IConnectionEstablishedEvent h );
  void unsubscribe( IConnectionEstablishedEvent h );

  void subscribe( IConnectionDisposedEvent h );
  void unsubscribe( IConnectionDisposedEvent h );

  void subscribe( IScannerChangeEvent h );
  void unsubscribe( IScannerChangeEvent h );
}
