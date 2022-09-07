package melsec.net;

import melsec.Driver;
import melsec.bindings.BaseTest;
import melsec.Config;
import melsec.events.EventType;
import melsec.events.driver.IDriverStartedEvent;
import melsec.events.driver.IDriverStoppedEvent;
import melsec.events.net.IConnectionDisposedEvent;
import melsec.utils.ResettableCountDownLatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static melsec.events.EventType.*;

public class ConnectionTest extends BaseTest {
  //region Class utility methods

  /**
   *
   */
  private Driver createDriver() {
    Config config = null;

    try {
      config = Config
        .builder()
        .address( "127.21.5.7" )
        .port( 5000 )
        .build();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }

    return new Driver( config );
  }
  //endregion

  //region Class tests
  @Test
  public void Should_RecvDriverStartedEvent() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IDriverStartedEvent)(x ) -> lock.countDown() );

    d.start();

    lock.await();
  }

  @Test
  public void ShouldNot_RecvDriverStartedEvent() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IDriverStartedEvent)( x ) -> lock.countDown() );

    lock.await( 100, TimeUnit.MILLISECONDS );
  }

  @Test
  public void Should_RecvDriverStopped() throws InterruptedException {
    var lock = new CountDownLatch( 2 );
    var registeredEvents = new ArrayList<>();
    var d = createDriver();

    d.events().subscribe( (IDriverStartedEvent)( x ) ->{
      registeredEvents.add( DriverStarted );
      lock.countDown();
    } );

    d.events().subscribe( (IDriverStoppedEvent) (x ) -> {
      registeredEvents.add( DriverStopped );
      lock.countDown();
    } );

    EventType[] expectedEvents = { DriverStarted, DriverStopped };

    d.start();

    d.stop();

    lock.await();

    Assertions.assertIterableEquals( registeredEvents, Arrays.asList(expectedEvents));
  }

  @Test
  public void ShouldNot_RecvDriverStopped() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe( (IDriverStartedEvent)( x ) -> lock.countDown() );

    d.events().subscribe( ( IDriverStoppedEvent) ( x ) -> lock.countDown() );

    d.start();

    d.stop();

    lock.await( 100, TimeUnit.MILLISECONDS );
  }

  @Test
  public void ShouldNot_RecvDriverStopped2() throws InterruptedException {
    var lock = new CountDownLatch( 1 );
    var d = createDriver();

    d.events().subscribe((IDriverStoppedEvent) (x) -> lock.countDown());

    d.stop();

    lock.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void Should_RecvConnecting() throws InterruptedException {
    var d = createDriver();
    var lock = new CountDownLatch( 4 );
    var registeredEvents = new ArrayList<>();

    d.events().subscribe( (IDriverStartedEvent)( x ) ->{
      registeredEvents.add( DriverStarted );
      lock.countDown();
    } );

    d.events().subscribe( (IDriverStartedEvent)( x ) ->{
      registeredEvents.add( ConnectionConnecting );
      lock.countDown();
    } );

    d.events().subscribe( (IConnectionDisposedEvent ) (x ) -> {
      registeredEvents.add( ConnectionDisposed );
      lock.countDown();
    } );

    d.events().subscribe( ( IDriverStoppedEvent) ( x ) -> {
      registeredEvents.add( DriverStopped );
      lock.countDown();
    } );

    EventType[] expectedEvents =
      { DriverStarted, ConnectionConnecting, ConnectionDisposed, DriverStopped };

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

    d.events().subscribe( (IDriverStartedEvent)( x ) ->{
      registeredEvents.add( DriverStarted );
      lock.countDown();
    } );

    d.events().subscribe( (IDriverStartedEvent)( x ) ->{
      registeredEvents.add( ConnectionConnecting );
      lock.countDown();
    } );

    d.events().subscribe( (IConnectionDisposedEvent ) (x ) -> {
      registeredEvents.add( ConnectionDisposed );
      lock.countDown();
    } );

    d.events().subscribe( ( IDriverStoppedEvent) ( x ) -> {
      registeredEvents.add( DriverStopped );
      lock.countDown();
    } );

    EventType[] expectedEvents =
      { DriverStarted, ConnectionConnecting, ConnectionDisposed, DriverStopped };

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