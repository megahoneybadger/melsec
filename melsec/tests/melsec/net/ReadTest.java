package melsec.net;

import melsec.Config;
import melsec.Driver;
import melsec.bindings.BaseTest;
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

public class ReadTest extends BaseTest {
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
    } catch ( UnknownHostException e ) {
      throw new RuntimeException(e);
    }

    return new Driver( config );
  }

  //endregion

  //region Class tests
  @Test
  public void Should_Read_U2() throws InterruptedException {

    var d = createDriver();


//    var lock = new CountDownLatch( 1 );
//
//
//    d.events().subscribe( (IDriverStartedEvent)(x ) -> lock.countDown() );
//
//    d.start();
//
//    lock.await();
  }

  //endregion
}