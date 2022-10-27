package melsec.utils;

import melsec.bindings.IPlcObject;
import melsec.EquipmentClient;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.EquipmentServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public record ScanTestFrame( EquipmentClient client,
                             EquipmentServer server,
                             EquipmentScanner scanner,
                             CountDownLatch lock,
                             ArrayList<List<IPlcObject>> results,
                             MemoryRandomUpdater changer ) {

  public void await() throws InterruptedException {
    lock.await();
  }
  /**
   *
   */
  public void assertScanResults() {
    var list = changer.getChanges();

    assertEquals(list.size(), results().size());

    for(int i = 0; i < list.size(); ++i) {
      var nextListScan = results.get(i);
      var nextListWrite = list.get( i );

      if( nextListScan.size() != nextListWrite.size() ){
        var set = new HashSet<>( nextListScan
          .stream()
          .map( x -> UtilityHelper.getCoordinate( x ) )
          .toList());

        for( var y : nextListWrite ){
          if( !set.contains( UtilityHelper.getCoordinate( y ) ) ){
            System.out.println( y );
            System.out.println( nextListWrite.indexOf( y ) );

          }
        }
      }

      assertEquals(nextListScan.size(), nextListWrite.size());

      for(int j = 0; j < nextListScan.size(); ++j){
        var nextItemScan = nextListScan.get( j );
        var nextItemWrite = nextListWrite.get( j );

        assertEquals(nextItemScan,nextItemWrite);
      }
    }
  }

}
