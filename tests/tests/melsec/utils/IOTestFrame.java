package melsec.utils;

import melsec.EquipmentClient;
import melsec.bindings.IPlcObject;
import melsec.types.io.IORequest;
import melsec.types.io.IOResponseItem;
import melsec.simulation.EquipmentServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public record IOTestFrame(EquipmentClient client,
                          EquipmentServer server,
                          CountDownLatch lock,
                          ArrayList<IOResponseItem> results ) {

  //region Class 'IO' methods

  /**
   * @param proto
   */
  public void readAsync(IPlcObject proto) {
    client.exec(IORequest
      .builder()
      .read(proto)
      .complete(y -> {
        results.addAll(UtilityHelper.toList(y.items()));
        lock.countDown();
      })
      .build());
  }

  /**
   * @param proto
   */
  public void readAsync(List<IPlcObject> proto) {
    client.exec(IORequest
      .builder()
      .read(proto)
      .complete(y -> {
        results.addAll( UtilityHelper.toList(y.items()) );
        lock.countDown();
      })
      .build());
  }

  /**
   * @throws InterruptedException
   */
  public void await( int unit ) throws InterruptedException {
    if( 0 == unit ){
      lock.await();
    } else{
      lock.await(unit, TimeUnit.MILLISECONDS);
    }
  }

  /**
   *
   * @throws InterruptedException
   */
  public void await() throws InterruptedException {
    await( 300 );
  }
  //endregion

  //region Class 'Assert' methods

  /**
   * @param o
   */
  public void assertResults(IPlcObject o) {
    var list = new ArrayList<IPlcObject>();
    list.add(o);

    assertResults(list);
  }

  /**
   *
   * @param arr
   */
  public void assertResults(IPlcObject [] arr) {
    assertResults(Arrays.stream( arr ).toList());
  }

  /**
   * @param list
   */
  public void assertResults(List<IPlcObject> list) {
    assertEquals(list.size(), results().size());

    for(int i = 0; i < list.size(); ++i) {
      var item = results.get(i);

      assertTrue(item.result().success());
      assertEquals(item.result().value(),list.get(i));
    }
  }
  //endregion

}
