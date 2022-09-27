package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcString;
import melsec.bindings.PlcU4;
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


public class ReadString extends BaseIOTest {

  //region Class 'String' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Read_String_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_OddSize() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 1, STRING_1 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_OddSize2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 3, STRING_1 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_OddSize3() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 3, "a" );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_EvenSize() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 2, STRING_1 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_EvenSize2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 4, STRING_2 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_EvenSize4() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 4, "ab" );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_EmptyString() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 0, STRING_1 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_EmptyString2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 10, null );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcU4( WordDeviceCode.W, ADDRESS_2, RandomFactory.getString());
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_String_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 12 );
    server.write(toWrite);

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleString_1() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getString( 10, 10 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleString_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getString( 10, 100 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleString_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getString( 10, 1000 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleString_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getString( 10, 5000 );

    server.write( toWrite );

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleString_5() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleString_3();
    }
  }

  @Test
  public void Should_NotRead_String_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.success() );
    assertFalse( res.value().equals( toWrite ) );
  }

  @Test
  public void Should_NotRead_String_TooManyPoints() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 5000, STRING_1 );

    var toRead = toWrite.without();

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_TOO_MANY_POINTS));
  }
  //endregion
}

