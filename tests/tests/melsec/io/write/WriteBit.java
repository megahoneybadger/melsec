package melsec.io.write;

import melsec.bindings.PlcBit;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteBit extends BaseIOTest {

  //region Class 'Bit' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Write_Bit_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.M, 0, true );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Bit_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( BitDeviceCode.B );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Bit_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, Memory.MAX_BITS - 1, true );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Bit_MaxAddressLeft() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, 0, true );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_Bit_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, -456, true );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_NotWrite_Bit_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, 1 << 24, true );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Write_MultipleBit_1() throws InvalidRangeException, InterruptedException {
    PlcBit[] toWrite = {
      new PlcBit( BitDeviceCode.B, ADDRESS_1, true ),
      new PlcBit( BitDeviceCode.M, ADDRESS_1, false ),
      new PlcBit( BitDeviceCode.L, ADDRESS_1, true )
    };

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBit_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBit_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBit_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBit_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 150000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBit_6() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleBit_4();
    }
  }

  @Test
  public void Should_NotWrite_Bit_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.M, 0, true );

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

