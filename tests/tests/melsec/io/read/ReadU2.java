package melsec.io.read;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcU2;
import melsec.io.BaseIOTest;
import melsec.types.exceptions.InvalidRangeException;
import melsec.types.WordDeviceCode;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.*;


public class ReadU2 extends BaseIOTest {

  //region Class 'U2' methods
  /**
   */
  @Test
  public void Should_Read_U2_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2(WordDeviceCode.W, ADDRESS_1, 200);
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, RandomFactory.getU2());
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U2_Max() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, 0xFFFF);
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_U2_Min() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2( WordDeviceCode.W, ADDRESS_2, 0 );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }


  @Test
  public void Should_Read_MultipleU2_1() throws InvalidRangeException, InterruptedException {
    IPlcObject[] toWrite = {
      new PlcU2( WordDeviceCode.W, ADDRESS_1, 500 ),
      new PlcU2( WordDeviceCode.R, ADDRESS_1, 600 ),
      new PlcU2( WordDeviceCode.D, ADDRESS_1, 700 )
    };

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 10 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 100 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 200 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 500 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcU2( 150000 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleU2_7() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleU2_5();
    }
  }
  @Test
  public void Should_NotRead_U2_BadRange() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2(WordDeviceCode.W, INVALID_ADDRESS_1, 200);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_NotRead_U2_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU2(WordDeviceCode.W, ADDRESS_1, 200);

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

