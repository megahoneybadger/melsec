package melsec.io.write;

import melsec.bindings.IPlcWord;
import melsec.bindings.PlcI2;
import melsec.bindings.PlcI4;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class WriteI4 extends BaseIOTest {

  //region Class 'I4' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Write_I4_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, 0, 114547 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_1, RandomFactory.getI4() );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I4_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_2, Integer.MAX_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I4_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_1, Integer.MIN_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I4_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, Memory.MAX_WORDS - 2, Integer.MAX_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_I4_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, -1500, -457 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_NotWrite_I4_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R,  1 << 24, 542 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Write_MultipleI4_1() throws InvalidRangeException, InterruptedException {
    IPlcWord[] toWrite = {
      new PlcI4( WordDeviceCode.W, ADDRESS_1, 5084 ),
      new PlcI4( WordDeviceCode.R, ADDRESS_1, -6541 ),
      new PlcI4( WordDeviceCode.D, ADDRESS_1, -870 )
    };

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI4_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI4_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI4_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 50000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI4_6() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleI4_4();
    }
  }

  @Test
  public void Should_NotWrite_I4_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4();

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

