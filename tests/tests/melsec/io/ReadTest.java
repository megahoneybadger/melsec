package melsec.io;

import melsec.ClientOptions;
import melsec.EquipmentClient;
import melsec.bindings.BaseTest;
import melsec.bindings.IPlcObject;
import melsec.bindings.PlcU2;
import melsec.types.events.net.IConnectionEstablishedEvent;
import melsec.types.exceptions.InvalidRangeException;
import melsec.simulation.ServerOptions;
import melsec.simulation.EquipmentServer;
import melsec.types.WordDeviceCode;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;
import melsec.utils.Copier;
import melsec.utils.IOTestFrame;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static melsec.utils.RandomFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReadTest extends BaseTest {

  //region Class constants
  /**
   *
   */
  private final static int PORT = 5000;
  //endregion

  //region Class members
  /**
   *
   */
  private EquipmentServer server;
  /**
   *
   */
  private EquipmentClient client;
  //endregion

  //region Class initialization

  /**
   * @throws IOException
   * @throws InterruptedException
   */
  @BeforeAll
  private void initAll() throws IOException, InterruptedException {
    server = new EquipmentServer(ServerOptions
      .builder()
      .port(PORT)
      .logger(false)
      .build());

    client = new EquipmentClient(ClientOptions
      .builder()
      .address("127.0.0.1")
      .port(PORT)
      //.loggers( new ConsoleLogger( LogLevel.INFO ) )
      .build());

    var lock = new CountDownLatch(1);

    client
      .events()
      .subscribe((IConnectionEstablishedEvent) (x) -> lock.countDown());

    server.start();
    client.start();

    lock.await();
  }

  /**
   * @throws IOException
   */
  @AfterAll
  private void cleanAll() throws IOException {
    server.stop();
    client.stop();
  }

  /**
   *
   */
  @BeforeEach
  private void initEach() {
    server.reset();
  }

  /**
   *
   */
  @AfterEach
  private void cleanEach() {

  }

  /**
   * @return
   */
  private IOTestFrame createFrame() {
    return createFrame(1);
  }

  /**
   * @param iAsyncSteps
   * @return
   */
  private IOTestFrame createFrame(int iAsyncSteps) {
    return new IOTestFrame(client, server,
      new CountDownLatch(iAsyncSteps), new ArrayList<>());
  }
  //endregion

  //region Class 'U2' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Read_U2_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2(WordDeviceCode.W, ADDRESS_1, 200);
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_U2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, RandomFactory.getU2());
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_1() throws InvalidRangeException, InterruptedException {
    IPlcObject[] toWrite = {
      new PlcU2( WordDeviceCode.W, ADDRESS_1, 500 ),
      new PlcU2( WordDeviceCode.R, ADDRESS_1, 600 ),
      new PlcU2( WordDeviceCode.D, ADDRESS_1, 700 )
    };

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 10 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 100 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 200 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 500 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 50000 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }



  //endregion
}

