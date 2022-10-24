package melsec.io.write;

import melsec.bindings.IPlcWord;
import melsec.bindings.PlcI2;
import melsec.bindings.PlcString;
import melsec.bindings.files.BindingSerializer;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BindingSerializationException;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WriteString extends BaseIOTest {

  //region Class 'String' methods

  @Test
  public void Should_Write_String_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, 0, SIZE_1, STRING_2, STRING_1 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_OddLength() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( SIZE_ODD );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_EvenLength() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( SIZE_EVEN );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_Empty() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 0 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_Empty2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 10, null );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_2() throws InvalidRangeException, InterruptedException {
    var s = RandomFactory.getString();
    var toWrite = new PlcString( WordDeviceCode.W, 0, s.length(), s );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_String_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 12 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleString_1() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 10, 15, 10 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleString_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 10, 15, 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleString_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 10, 15, 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleString_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcString( 10, 15, 5000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleString_5() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Write_MultipleString_3();
    }
  }

  @Test
  public void Should_NotWrite_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 5000, STRING_1 );

    var f = createFrame();
    //f.writeAsync( toWrite );
    f.decreaseLock();
    f.await();

    var serverValue = server.read( toWrite );
    var writtenValue = Valuer.getValue( toWrite );

    assertNotEquals( serverValue, writtenValue );
  }

  @Test
  public void Should_NotWrite_TooManyPoints() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcString( WordDeviceCode.W, ADDRESS_1, 5000, STRING_1 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_TOO_MANY_POINTS));
  }
  //endregion
}

