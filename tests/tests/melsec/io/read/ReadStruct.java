package melsec.io.read;

import melsec.bindings.PlcStruct;
import melsec.io.BaseIOTest;
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


public class ReadStruct extends BaseIOTest {

  //region Class 'Struct' methods
  /**
   */
  @Test
  public void Should_Read_Struct_1() throws InvalidRangeException, InterruptedException {
    var toWrite = PlcStruct
      .builder( WordDeviceCode.W, 0x100, "Glass")
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

    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Struct_2() throws InvalidRangeException, InterruptedException {
    var toWrite = PlcStruct
      .builder( WordDeviceCode.W, 0x200, "Glass")
      .offset(2)
      .u2(RandomFactory.getU2())
      .u2(RandomFactory.getU2())
      .i4(RandomFactory.getI2())
      .u2(RandomFactory.getU2())
      .offset(30)
      .i2(RandomFactory.getI2())
      .offset(11)
      .i4(RandomFactory.getI4())
      .string( 10, RandomFactory.getString( 10 ) )
      .build();

    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Struct_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct();

    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Struct_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 45 );

    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleStruct_1() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10, 10 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleStruct_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10, 100 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleStruct_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10, 1000 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_NotRead_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 10 );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.success() );
    assertFalse( res.value().equals( toWrite ) );
  }

  @Test
  public void Should_NotRead_Struct_TooManyPoints() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcStruct( 1000 );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_TOO_MANY_POINTS));
  }
  //endregion
}

