package melsec.io.write;

import melsec.bindings.IPlcWord;
import melsec.bindings.PlcI2;
import melsec.bindings.files.BindingSerializer;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BindingSerializationException;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class WriteI2 extends BaseIOTest {

  //region Class 'I2' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Write_I2_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, 0, ( short )114 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_2, RandomFactory.getI2() );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I2_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_2, Short.MAX_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I2_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_1, Short.MIN_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_I2_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2(  WordDeviceCode.W, Memory.MAX_WORDS - 1, Short.MAX_VALUE );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_I2_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.R, -1500, (short)-457 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_NotWrite_I2_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.R,  1 << 24, ( short )542 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Write_MultipleI2_1() throws InvalidRangeException, InterruptedException {
    IPlcWord[] toWrite = {
      new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short )5084 ),
      new PlcI2( WordDeviceCode.R, ADDRESS_1, ( short )-6541 ),
      new PlcI2( WordDeviceCode.D, ADDRESS_1, ( short )-870 )
    };

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI2_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI2_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI2_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 150000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleI2_6() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleI2_4();
    }
  }

  @Test
  public void Should_NotWrite_I2_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2();

    var f = createFrame();
    //f.writeAsync( toWrite );
    f.decreaseLock();
    f.await();

    var serverValue = server.read( toWrite );
    var writtenValue = Valuer.getValue( toWrite );

    assertNotEquals( serverValue, writtenValue );
  }

  //@Test
  public void GenerateBigBindingFile() throws InvalidRangeException, InterruptedException, BindingSerializationException {

    var list = RandomFactory.getPlcBit( 10000 );

    Collections.sort( list, ( a, b ) -> ( a.device() == b.device() ) ?
      a.address() - b.address() : a.device().value() - b.device().value()  );

    BindingSerializer.write( "../.resources/conf_big/file1.xml", list );
  }
  //endregion
}

