package melsec.io.scan;

import melsec.bindings.IPlcObject;
import melsec.io.BaseIOTest;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.PlcRegion;
import melsec.types.WordDeviceCode;
import melsec.utils.MemoryRandomUpdater;
import melsec.utils.RandomFactory;
import melsec.utils.ScanTestFrame;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class BaseScanTest  extends BaseIOTest {

  //region Class members
  /**
   *
   */
  protected EquipmentScanner scanner;
  //endregion

  //region Class initialization
  /**
   *
   */
  @AfterEach
  protected void cleanEach() {
    if( null != scanner ){
      scanner.dispose();
    }
  }
  //endregion

  //region Class utility methods
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
  /**
   *
   * @return
   */
  protected List<PlcRegion> getBitRegions(){
    var res = new ArrayList<PlcRegion>();

    for( var d : BitDeviceCode.values() ){
      res.add( getBitRegion( d, 0, Memory.MAX_BITS ));
    }

    return res;
  }
  /**
   *
   * @param device
   * @param start
   * @param size
   * @return
   */
  protected PlcRegion getBitRegion( BitDeviceCode device, int start, int size ){
    return new PlcRegion( device, start, size );
  }
  /**
   *
   * @return
   */
  protected List<PlcRegion> getWordRegions(){
    var res = new ArrayList<PlcRegion>();

    for( var d : WordDeviceCode.values() ){
      res.add( getWordRegion( d, 0, Memory.MAX_WORDS ));
    }

    //res.add( getWordRegion( WordDeviceCode.R, 0, 12000 ));

    return res;
  }
  /**
   *
   * @param device
   * @param start
   * @param size
   * @return
   */
  protected PlcRegion getWordRegion( WordDeviceCode device, int start, int size ){
    return new PlcRegion( device, start, size );
  }
  /**
   *
   * @return
   */
  protected List<PlcRegion> getAllRegions(){
    return Stream
      .of(getBitRegions(), getWordRegions())
      .flatMap( Collection::stream )
      .toList();
  }
  //endregion
}
