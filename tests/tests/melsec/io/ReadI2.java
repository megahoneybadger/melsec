package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcI2;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReadI2 extends BaseIOTest {

  //region Class 'I2' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Read_I2_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short )-3542 );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_2, RandomFactory.getI2());
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }
  @Test
  public void Should_Read_I2_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_2, Short.MAX_VALUE);
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I2_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2( WordDeviceCode.W, ADDRESS_2, Short.MIN_VALUE);
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }


  @Test
  public void Should_Read_MultipleU2_1() throws InvalidRangeException, InterruptedException {
    IPlcObject[] toWrite = {
      new PlcI2( WordDeviceCode.W, ADDRESS_1, (short)-500 ),
      new PlcI2( WordDeviceCode.R, ADDRESS_1, (short)600 ),
      new PlcI2( WordDeviceCode.D, ADDRESS_1, (short)-28700 )
    };

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_2() throws Exception {
    var toWrite = RandomFactory.getPlcI2( 10 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI2_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 100 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI2_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 500 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI2_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI2( 150000 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI2_7() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleI2_5();
    }
  }

  @Test
  public void Should_NotRead_I2_BadRange() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI2(WordDeviceCode.W, INVALID_ADDRESS_1, ( short )-200);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_NotRead_I_NoData() throws InterruptedException {
    var toWrite = new PlcI2(WordDeviceCode.W, ADDRESS_1, ( short )200);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.success() );
    assertFalse( res.value().equals( toWrite ) );
  }
  //endregion
}

