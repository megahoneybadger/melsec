package melsec.net;

import melsec.bindings.BaseTest;
import melsec.types.events.EventType;
import melsec.types.events.client.IClientStartedEvent;
import melsec.types.events.client.IClientStoppedEvent;
import melsec.types.events.net.IConnectionDisposedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ResettableCountDownLatch;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static melsec.types.events.EventType.*;

public class ConnectionTest extends BaseTest {
  //region Class utility methods

  /**
   *
   */
  private EquipmentClient createDriver() {
    ClientOptions config = null;

    try {
      config = ClientOptions
        .builder()
        .address( "127.21.5.7" )
        .port( 5000 )
        .build();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }

    return new EquipmentClient( config );
  }
  //endregion

  //region Class tests
  @Test
  public void Should_RecvDriverStartedEvent() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IClientStartedEvent)(x ) -> lock.countDown() );

    d.start();

    lock.await();
  }

  @Test
  public void ShouldNot_RecvDriverStartedEvent() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IClientStartedEvent)(x ) -> lock.countDown() );

    lock.await( 100, TimeUnit.MILLISECONDS );
  }

  @Test
  public void Should_RecvDriverStopped() throws InterruptedException {
    var lock = new CountDownLatch( 2 );
    var registeredEvents = new ArrayList<>();
    var d = createDriver();

    d.events().subscribe( (IClientStartedEvent)(x ) ->{
      registeredEvents.add(ClientStarted);
      lock.countDown();
    } );

    d.events().subscribe( (IClientStoppedEvent) (x ) -> {
      registeredEvents.add(ClientStopped);
      lock.countDown();
    } );

    EventType[] expectedEvents = {ClientStarted, ClientStopped};

    d.start();

    d.stop();

    lock.await();

    Assertions.assertIterableEquals( registeredEvents, Arrays.asList(expectedEvents));
  }

  @Test
  public void ShouldNot_RecvDriverStopped() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IClientStartedEvent)(x ) -> lock.countDown() );

    d.events().subscribe( (IClientStoppedEvent) (x ) -> lock.countDown() );

    d.start();

    d.stop();

    lock.await( 100, TimeUnit.MILLISECONDS );
  }

  @Test
  public void ShouldNot_RecvDriverStopped2() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe((IClientStoppedEvent) (x) -> lock.countDown());

    d.stop();

    lock.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void Should_RecvConnecting() throws InterruptedException {
    var d = createDriver();
    var lock = new CountDownLatch( 4 );
    var registeredEvents = new ArrayList<>();

    d.events().subscribe( (IClientStartedEvent)(x ) ->{
      registeredEvents.add(ClientStarted);
      lock.countDown();
    } );

    d.events().subscribe( (IClientStartedEvent)(x ) ->{
      registeredEvents.add( ConnectionConnecting );
      lock.countDown();
    } );

    d.events().subscribe( (IConnectionDisposedEvent ) (x ) -> {
      registeredEvents.add( ConnectionDisposed );
      lock.countDown();
    } );

    d.events().subscribe( (IClientStoppedEvent) (x ) -> {
      registeredEvents.add(ClientStopped);
      lock.countDown();
    } );

    EventType[] expectedEvents =
      {ClientStarted, ConnectionConnecting, ConnectionDisposed, ClientStopped};

    d.start();

    Thread.sleep( 200 );

    d.stop();

    lock.await();

    Assertions.assertIterableEquals( registeredEvents, Arrays.asList(expectedEvents));
  }

  @Test
  public void Should_RecvConnectingMultipleTimes() throws InterruptedException {
    var d = createDriver();
    final ResettableCountDownLatch lock = new ResettableCountDownLatch( 4 );
    var registeredEvents = new ArrayList<>();

    d.events().subscribe( (IClientStartedEvent)(x ) ->{
      registeredEvents.add(ClientStarted);
      lock.countDown();
    } );

    d.events().subscribe( (IClientStartedEvent)(x ) ->{
      registeredEvents.add( ConnectionConnecting );
      lock.countDown();
    } );

    d.events().subscribe( (IConnectionDisposedEvent ) (x ) -> {
      registeredEvents.add( ConnectionDisposed );
      lock.countDown();
    } );

    d.events().subscribe( (IClientStoppedEvent) (x ) -> {
      registeredEvents.add(ClientStopped);
      lock.countDown();
    } );

    EventType[] expectedEvents =
      {ClientStarted, ConnectionConnecting, ConnectionDisposed, ClientStopped};

    for( int i = 0; i < 3; ++i ){
      registeredEvents.clear();
      lock.reset();

      d.start();

      Thread.sleep( 200 );

      d.stop();

      lock.await();

      Assertions.assertIterableEquals( registeredEvents, Arrays.asList(expectedEvents));
    }
  }
  //endregion
}