package melsec.io;

import melsec.ClientOptions;
import melsec.EquipmentClient;
import melsec.bindings.BaseTest;
import melsec.simulation.EquipmentServer;
import melsec.simulation.ServerOptions;
import melsec.types.events.net.IConnectionEstablishedEvent;
import melsec.utils.IOTestFrame;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIOTest extends BaseTest {
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
  protected EquipmentServer server;
  /**
   *
   */
  protected EquipmentClient client;
  //endregion

  //region Class initialization

  /**
   * @throws IOException
   * @throws InterruptedException
   */
  @BeforeAll
  private void initAll() throws IOException, InterruptedException {
    server = createServer();

    client = createClient();

    var lock = new CountDownLatch(1);

    client
      .events()
      .subscribe((IConnectionEstablishedEvent) (x) -> lock.countDown());

    server.start();
    client.start();

    lock.await();
  }

  /**
   *
   * @return
   * @throws UnknownHostException
   */
  protected EquipmentClient createClient() throws UnknownHostException {
    return new EquipmentClient(ClientOptions
      .builder()
      .address("127.0.0.1")
      .port(PORT)
      //.loggers( new ConsoleLogger( LogLevel.INFO ) )
      .build());
  }

  protected EquipmentServer createServer(){
    return new EquipmentServer(ServerOptions
      .builder()
      .port(PORT)
      .logger(false)
      .build());
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
  private void initEach() throws IOException {
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
  protected IOTestFrame createFrame() {
    return createFrame(1);
  }

  /**
   * @param iAsyncSteps
   * @return
   */
  protected IOTestFrame createFrame(int iAsyncSteps) {
    return new IOTestFrame(client, server,
      new CountDownLatch(iAsyncSteps), new ArrayList<>());
  }
  //endregion
}
