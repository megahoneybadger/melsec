package melsec.io.write;

import melsec.bindings.IPlcWord;
import melsec.bindings.PlcU2;
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


public class WriteU2 extends BaseIOTest {

  //region Class 'U2' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Write_U2_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, 0, 114 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, RandomFactory.getU2() );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U2_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, 0xFFFF );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U2_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_1, 0 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_U2_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2(  WordDeviceCode.W, Memory.MAX_WORDS - 1, 0xFFFF );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_U2_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.R, -1500, 457 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_NotWrite_U2_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.R,  1 << 24, 542 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Write_MultipleU2_1() throws InvalidRangeException, InterruptedException {
    IPlcWord[] toWrite = {
      new PlcU2( WordDeviceCode.W, ADDRESS_1, 5084 ),
      new PlcU2( WordDeviceCode.R, ADDRESS_1, -6541 ),
      new PlcU2( WordDeviceCode.D, ADDRESS_1, -870 )
    };

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU2_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU2_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU2_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 150000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleU2_6() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleU2_4();
    }
  }

  @Test
  public void Should_NotWrite_U2_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2();

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

