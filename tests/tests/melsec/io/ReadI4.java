package melsec.io;

import melsec.bindings.*;
import melsec.simulation.Memory;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReadI4 extends BaseIOTest {

  //region Class 'I4' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Read_I4_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_1, -548796 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_2, RandomFactory.getI4());
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I4_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, ADDRESS_2, Integer.MAX_VALUE );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I4_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, ADDRESS_1, 0 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I4_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.W, Memory.MAX_WORDS - 2, Integer.MAX_VALUE );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_I4_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, -1500, -457238 );

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_I4_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, 1 << 24, 238 );

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_I4_BadRange() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, 1 << 24 - 1, 238 );

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_Read_I4_BadRange2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcI4( WordDeviceCode.R, Memory.MAX_WORDS - 1, 23887 );
    //server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_Read_MultipleI4_1() throws InvalidRangeException, InterruptedException {
    IPlcWord[] toWrite = {
      new PlcI4( WordDeviceCode.W, ADDRESS_1, 50845120 ),
      new PlcI4( WordDeviceCode.R, ADDRESS_1, 65412700 ),
      new PlcI4( WordDeviceCode.D, ADDRESS_1, 70870 )
    };

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 10 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 100 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 200 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 500 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcI4( 75000 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleI4_7() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleI4_5();
    }
  }

  @Test
  public void Should_NotRead_I4_NoData() throws InterruptedException {
    var toWrite = new PlcI4(WordDeviceCode.W, ADDRESS_1, 20087475);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.success() );
    assertFalse( res.value().equals( toWrite ) );
  }
  //endregion
}

