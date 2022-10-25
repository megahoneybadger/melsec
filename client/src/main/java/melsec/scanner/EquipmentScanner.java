package melsec.scanner;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBinary;
import melsec.commands.batch.BatchReadCommand;
import melsec.net.EquipmentClient;
import melsec.types.*;
import melsec.types.events.EventType;
import melsec.types.events.scanner.IScannerChangeEvent;
import melsec.types.events.scanner.ScannerEventArgs;
import melsec.types.exceptions.BaseException;
import melsec.types.io.IORequest;
import melsec.types.io.IOResponse;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;
import org.apache.logging.log4j.LogManager;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// scan-start -f "bits.xml" -r w100 500, b100 500
// scan-stop

public class EquipmentScanner {

  //region Class members
  /**
   *
   */
  private boolean run;
  /**
   *
   */
  private EquipmentClient client;
  /**
   *
   */
  private Cache cache;
  /**
   *
   */
  private IORequest request;
  /**
   *
   */
  private int timeout;
  /**
   *
   */
  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  /**
   *
   */
  private IScannerChangeEvent eventHandler;
  private Instant start;
  private Instant end;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public static Builder builder(){
    return new Builder();
  }
  //endregion

  //region Class initialization
  /**
   *
   */
  private EquipmentScanner(){
    run = true;
  }
  /**
   *
   */
  public void dispose(){
    run = false;

    LogManager.getLogger().info( "Scanner stopped" );
  }
  //endregion

  //region Class 'Timer' methods
  private void scan(){
    scan( false );
  }
  /**
   *
   */
  private void scan( boolean useBackoff ){
    if( !run )
      return;

    var actualTimeout = useBackoff ?
      Math.max( 5000, timeout ) : timeout;

    executor.schedule( () -> {
      start = Instant.now();
      client.exec( request );
      }, actualTimeout, TimeUnit.MILLISECONDS );
  }
  /**
   *
   * @param response
   */
  private void onScanComplete( IOResponse response ){
    try{
      var bins = UtilityHelper
        .toStream( response.items())
        .filter( x -> x.result().success() )
        .filter( x -> x.result().value().type() == DataType.Binary )
        .map( x -> ( PlcBinary )x.result().value() )
        .toList();

      var changes = cache.update( bins );
      var args = new ScannerEventArgs( changes );
      end = Instant.now();

      //System.out.println(Duration.between(start, end).toMillis());

      if( changes.size() > 0 ){
        client
          .events()
          .enqueue( EventType.ScannerChanges, args );
      }

      if( null != eventHandler ){
        eventHandler.executed( args );
      }
    }
    catch( Exception exc ){
      LogManager.getLogger().error( "Failed to complete scan cycle: {}", exc.toString() );
    }
    finally {
      scan(shouldUseBackoff( response ));
    }
  }
  /**
   *
   * @param response
   * @return
   */
  private boolean shouldUseBackoff( IOResponse response ){
    var items = UtilityHelper.toList( response.items());

    if( null == items || items.size() == 0 )
      return false;

    var last = items.get( items.size() - 1 );

    if( last.result().success() )
      return false;

    var exc = last.result().error();

    if( !( exc instanceof BaseException ) )
      return false;

    return (( BaseException )exc).getCode() == ErrorCode.ConnectionNotEstablished;
  }
  //endregion

  //region Class internal structs
  /**
   *
   */
  public static class Builder{

    //region Class members
    /**
     *
     */
    private ArrayList<IPlcObject> bindings = new ArrayList<>();
    /**
     *
     */
    private ArrayList<PlcRegion> regions = new ArrayList();
    /**
     *
     */
    private int timeout;
    /**
     *
     */
    private IScannerChangeEvent eventHandler;
    //endregion

    //region Class 'Add' methods
    /**
     *
     * @param device
     * @param start
     * @param size
     */
    public Builder region( IDeviceCode device, int start, int size ){
      regions.add( new PlcRegion( device, start, size ) );
      return this;
    }
    /**
     *
     * @param arr
     * @return
     */
    public Builder region( PlcRegion... arr ){
      return region( List.of( arr ) );
    }
    /**
     *
     * @param list
     * @return
     */
    public Builder region( List<PlcRegion> list ){
      regions.addAll( list );
      return this;
    }
    /**
     *
     * @param o
     * @return
     */
    public Builder binding( IPlcObject o ){
      bindings.add( o );

      return this;
    }
    /**
     *
     * @param arr
     * @return
     */
    public Builder binding( IPlcObject...arr ){
      return binding( List.of( arr ) );
    }
    /**
     *
     * @param list
     * @return
     */
    public Builder binding( List<IPlcObject> list ){
      bindings.addAll( list );
      return this;
    }
    /**
     *
     * @param t
     * @return
     */
    public Builder timeout( int t ){
      timeout = t;
      return this;
    }
    /**
     *
     * @param e
     * @return
     */
    public Builder changed( IScannerChangeEvent e){
      eventHandler = e;

      return this;
    }
    //endregion

    //region Class 'Build' methods
    /**
     *
     * @param client
     * @return
     */
    public EquipmentScanner build( EquipmentClient client ){
      var s = new EquipmentScanner();

      s.client = client;

      s.cache = new Cache( UtilityHelper.coalesce( bindings, new ArrayList<>() ) );

      var regs = UtilityHelper.coalesce( regions, new ArrayList<PlcRegion>() );

      var bins = toBinaries( regs )
        .stream()
        .map( x -> ( IPlcObject) x )
        .toList();

      s.request = IORequest
        .builder()
        .read( bins )
        .complete( x -> s.onScanComplete( x ) )
        .build();

      s.timeout = timeout;
      s.eventHandler = eventHandler;

      LogManager.getLogger().info( "Scanner started {}", Stringer.toString( regs ) );

      s.scan();

      return s;
    }
    /**
     *
     * @return
     */
    private List<PlcBinary> toBinaries( List<PlcRegion> regions ){
      return regions
        .stream()
        .flatMap( x -> toBinaries( x ).stream() )
        .toList();
    }
    /**
     *
     * @param r
     * @return
     */
    private List<PlcBinary> toBinaries( PlcRegion r ){
      var res = new ArrayList<PlcBinary>();

      var start = r.start();
      var rem = r.size();

      var max = BatchReadCommand.MAX_POINTS;
      var step = r.device() instanceof BitDeviceCode ? max * 16 : max;

      while( rem > 0 ){
        var size = Math.min( rem, step );

        var next = new PlcBinary( r.device(), start, size );
        res.add( next );

        rem -= size;
        start += size;
      }

      return res;
    }
    //endregion
  }
  //endregion
}
