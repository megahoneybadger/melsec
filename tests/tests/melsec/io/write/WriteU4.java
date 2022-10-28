package melsec.io.write;

import melsec.bindings.IPlcWord;
import melsec.bindings.PlcU4;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WriteU4 extends BaseIOTest {

  //region Class 'U4' methods
  /**
   */
  @Test
  public void Should_Write_U4_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, 0, 114547l );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_1, RandomFactory.getU4() );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U4_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_2, 0xFFFF_FFFFl );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U4_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_1, 0l );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U4_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, Memory.MAX_WORDS - 2, 0xFFFF_FFFFl );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_U4_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, -1500, 457l );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_NotWrite_U4_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R,  1 << 24, 542l );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Write_MultipleU4_1() throws InvalidRangeException, InterruptedException {
    IPlcWord[] toWrite = {
      new PlcU4( WordDeviceCode.W, ADDRESS_1, 5084l ),
      new PlcU4( WordDeviceCode.R, ADDRESS_1, -6541l ),
      new PlcU4( WordDeviceCode.D, ADDRESS_1, -870l )
    };

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU4_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU4_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU4_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 50000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU4_6() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleU4_4();
    }
  }

  @Test
  public void Should_NotWrite_U4_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4();

    var f = createFrame();
    //f.writeAsync( toWrite );
    f.decreaseLock();
    f.await();

    var serverValue = server.read( toWrite );
    var writtenValue = Valuer.getValue( toWrite );

    assertNotEquals( serverValue, writtenValue );
  }

  //endregion
}

