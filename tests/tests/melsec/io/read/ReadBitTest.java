package melsec.io.read;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadBitTest extends BaseIOTest {

  //region Class 'Bit' methods

  @Test
  public void TestShould_Read_Bit_1() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, ADDRESS_2, true );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Bit_2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, ADDRESS_2, RandomFactory.getBit() );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Bit_MaxAddressRight() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, Memory.MAX_BITS - 1, RandomFactory.getBit() );
    server.write(toWrite);

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_Bit_BadAddress() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, -200, false );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_Bit_BadAddress2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, 1 << 24, false );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_ADDRESS));
  }

  @Test
  public void Should_Read_Bit_BadRange() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, 1 << 24 - 1, true );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    var res = f.results().get( 0 ).result();
    assertTrue( res.failure() );
    assertThat( res.error().getMessage(), CoreMatchers.containsString(ERROR_INVALID_RANGE));
  }

  @Test
  public void Should_Read_Bit_BadRange2() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, Memory.MAX_BITS, true );
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
  public void Should_Read_MultipleBit_1() throws InvalidRangeException, InterruptedException {
    IPlcObject[] toWrite = {
      new PlcBit( BitDeviceCode.B, ADDRESS_2, true ),
      new PlcBit( BitDeviceCode.L, ADDRESS_2, false ),
      new PlcBit( BitDeviceCode.M, ADDRESS_2, true )
    };

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 10 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 100 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 200 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_5() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 500 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_6() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcBit( 50000 );

    server.write( toWrite );

    var toRead = Copier.withoutValue( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await( 0 );

    f.assertReadResults( toWrite );
  }

  @Test
  public void Should_Read_MultipleBit_7() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_MultipleBit_5();
    }
  }

  @Test
  public void Should_NotRead_Bit_NoData() throws InvalidRangeException, InterruptedException {
    var toWrite = new PlcBit( BitDeviceCode.B, ADDRESS_2, true );

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
