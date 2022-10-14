package melsec.utils;

import melsec.bindings.IPlcObject;
import melsec.net.EquipmentClient;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.EquipmentServer;
import melsec.types.exceptions.InvalidRangeException;
import melsec.types.io.IORequest;
import melsec.types.io.IOResponseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

      assertEquals(nextListScan.size(), nextListWrite.size());

      for(int j = 0; j < nextListScan.size(); ++j){
        var nextItemScan = nextListScan.get( j );
        var nextItemWrite = nextListWrite.get( j );

        assertEquals(nextItemScan,nextItemWrite);
      }
    }
  }

}
