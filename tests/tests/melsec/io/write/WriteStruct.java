package melsec.io.write;

import melsec.bindings.PlcStruct;
import melsec.io.BaseIOTest;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.RandomFactory;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteStruct extends BaseIOTest {

  //region Class 'Struct' methods

  @Test
  public void Should_Write_Struct_1() throws InvalidRangeException, InterruptedException {
    var toWrite = PlcStruct
      .builder( WordDeviceCode.W, 0, "Glass")
      .offset(2)
      .u2(101)
      .u2(27894)
      .u2(31254)
      .offset(3)
      .i2((short) -1456)
      .offset(1)
      .i2((short) 5567)
      .string(4, "helloworld")
      .build();

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Struct_2() throws InvalidRangeException, InterruptedException {
    var toWrite = PlcStruct
      .builder( WordDeviceCode.W, 0x200, "Glass")
      .offset(2)
      .u2( RandomFactory.getU2())
      .u2(RandomFactory.getU2())
      .i4(RandomFactory.getI2())
      .u2(RandomFactory.getU2())
      .offset(30)
      .i2(RandomFactory.getI2())
      .offset(11)
      .i4(RandomFactory.getI4())
      .string( 10, RandomFactory.getString( 10 ) )
      .build();

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Struct_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 45 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Struct_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10, 100 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_Struct_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10, 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_NotWrite_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 1000 );

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
    var toWrite = RandomFactory.getPlcStruct( 1000 );

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_TOO_MANY_POINTS));
  }
  //endregion
}

