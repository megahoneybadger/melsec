package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.net.ClientOptions;
import melsec.net.EquipmentClient;
import melsec.bindings.BaseTest;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.EquipmentServer;
import melsec.simulation.ServerOptions;
import melsec.types.PlcRegion;
import melsec.types.events.net.IConnectionEstablishedEvent;
import melsec.utils.IOTestFrame;
import melsec.utils.MemoryRandomUpdater;
import melsec.utils.RandomFactory;
import melsec.utils.ScanTestFrame;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
  /**
   *
   */
  protected EquipmentScanner scanner;
  //endregion

  //region Class initialization

  /**
   * @throws IOException
   * @throws InterruptedException
   */
  @BeforeAll
  protected void initAll() throws IOException, InterruptedException {
    server = createServer();

    client = createClient();

    var lock = new CountDownLatch( 1 );

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
      //.loggers( new ConsoleLogger( LogLevel.DEBUG ) )
      .build());
  }
  /**
   *
   * @return
   */
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
    scanner.dispose();
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
  /**
   *
   * @param regions
   * @return
   */
  protected ScanTestFrame createScanFrame( List<PlcRegion> regions,
                                           List<IPlcObject> bindings,
                                           int cycles ){
    var timeout = 10;

    var lock = new CountDownLatch( cycles );
    var results = new ArrayList<List<IPlcObject>>();
    var changer = new MemoryRandomUpdater( server, bindings );

    scanner = EquipmentScanner
      .builder()
      .binding( bindings )
      .region( regions )
      .timeout( timeout )
      .changed( x -> {
        if( x.changes().size() > 0 ){
          var changes = new ArrayList<>( x.changes() );
          RandomFactory.sort( changes );
          results.add( changes );
        }

        lock.countDown();

        if( lock.getCount() > 0){
          changer.update();
        }

      } )
      .build( client );

    return new ScanTestFrame(client, server, scanner, lock, results, changer );
  }

  //endregion
}
