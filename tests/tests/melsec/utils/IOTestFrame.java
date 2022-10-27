package melsec.utils;

import melsec.EquipmentClient;
import melsec.bindings.IPlcObject;
import melsec.types.exceptions.InvalidRangeException;
import melsec.types.io.IORequest;
import melsec.types.io.IOResponseItem;
import melsec.simulation.EquipmentServer;
import melsec.types.io.IOResult;
import melsec.types.io.IOType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
   *
   * @param proto
   */
  public void writeAsync(IPlcObject proto) {
    writeAsync( List.of( proto ) );
  }
  /**
   *
   * @param arr
   */
  public void writeAsync(IPlcObject [] arr){
    writeAsync( List.of( arr ) );
  }
  /**
   *
   * @param proto
   */
  public void writeAsync(List<IPlcObject> proto) {
    client.exec(IORequest
      .builder()
      .write(proto)
      .complete(y -> {
        for( var item : y.items() ){
          if( item.result().success() ){
            var obj = Copier.withoutValue( item.result().value() );
            var value = server.read( obj );
            var newObj = Copier.withValue( obj, value );
            results.add( new IOResponseItem( IOType.Write, obj, IOResult.create( newObj ) ) );
          } else{
            results.add( item );
          }

        }

        //results.addAll(UtilityHelper.toList(y.items()));
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
    await( 0 );
  }
  /**
   *
   */
  public void decreaseLock(){
    lock.countDown();
  }
  //endregion

  //region Class 'Assert' methods

  /**
   * @param o
   */
  public void assertReadResults( IPlcObject o) {
    var list = new ArrayList<IPlcObject>();
    list.add(o);

    assertReadResults(list);
  }
  /**
   *
   * @param arr
   */
  public void assertReadResults( IPlcObject [] arr) {
    assertReadResults( List.of( arr ));
  }
  /**
   * @param list
   */
  public void assertReadResults( List<IPlcObject> list) {
    assertEquals(list.size(), results().size());

    for(int i = 0; i < list.size(); ++i) {
      var item = results.get(i);

      assertTrue(item.result().success());
      assertEquals(item.result().value(),list.get(i));
    }
  }
  /**
   *
   * @param o
   */
  public void assertWriteResults( IPlcObject o) throws InvalidRangeException {
    var list = new ArrayList<IPlcObject>();
    list.add(o);

    assertWriteResults(list);
  }
  /**
   *
   * @param arr
   */
  public void assertWriteResults( IPlcObject[] arr ) throws InvalidRangeException {
    assertWriteResults( List.of( arr ) );
  }
  /**
   *
   * @param list
   */
  public void assertWriteResults( List<IPlcObject> list ) throws InvalidRangeException {
    assertEquals(list.size(), results().size());

    for(int i = 0; i < list.size(); ++i) {
      var item = results.get(i);
      var next = list.get( i );

      var serverValue = server.read( next );
      var writtenValue = Valuer.getValue( next );

      if( !item.result().success() ){
        System.out.println( "fuck" );
      }

      assertTrue( item.result().success());
      assertEquals( serverValue, writtenValue );
    }
  }
  //endregion
}
