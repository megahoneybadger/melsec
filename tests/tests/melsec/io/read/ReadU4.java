package melsec.io.read;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcU4;
import melsec.io.BaseIOTest;
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


public class ReadU4 extends BaseIOTest {

  //region Class 'U4' methods
  /**
   */
  @Test
  public void Should_Read_U4_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_1, 548796l );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U4_4() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_2, RandomFactory.getU4());
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U4_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_2, 0xFFFF_FFFFl );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U4_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, ADDRESS_1, 0l );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U4_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, Memory.MAX_WORDS - 2, 0xFFFF_FFFFl );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U4_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, -500, 238l );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_U4_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, 1 << 24, 238l );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_U4_BadRange() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, 1 << 24 - 1, 238l );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_Read_U4_BadRange2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.R, Memory.MAX_WORDS - 1, 238l );
    //server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_Read_MultipleU4_1() throws InvalidRangeException, InterruptedException {
    IPlcObject[] toWrite = {
      new PlcU4( WordDeviceCode.W, ADDRESS_1, 50845120l ),
      new PlcU4( WordDeviceCode.R, ADDRESS_1, 65412700l ),
      new PlcU4( WordDeviceCode.D, ADDRESS_1, 70870l )
    };

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 10 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 100 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 200 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 500 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU4( 70000 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU4_7() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleU4_5();
    }
  }

  @Test
  public void Should_NotRead_U4_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4(WordDeviceCode.W, ADDRESS_1, 20087475l);

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

