package melsec.bindings;

import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.utils.ByteConverter;
import melsec.utils.Copier;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestI2 extends BaseTest {

  //region Class 'Test' methods
  @Test
  public void Should_CreateI2(){
    var n = new PlcI2( WordDeviceCode.W, ADDRESS_1 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == 0 );
    assertTrue( n.id().isEmpty() );
    assertEquals( 1, ByteConverter.getPointsCount( n ) );
    assertEquals( n.type(), DataType.I2 );
  }

  @Test
  public void Should_CreateI2_2() {
    var n = new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short ) -78 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == -78 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.I2 );
  }

  @Test
  public void Should_Be_Equal(){
    var n1 = new PlcI2( WordDeviceCode.W, ADDRESS_2, ( short )10 );

    var n2 = new PlcI2( WordDeviceCode.W, ADDRESS_2, ( short )10 );

    assertTrue( n1.equals( n2 ) );
  }

  @Test
  public void Should_Be_Equal2(){
    var n1 = new PlcI2( WordDeviceCode.W, ADDRESS_2, ( short )10 );

    var n2 = Copier.withValue( n1, ( short )10 );

    assertTrue( n1.equals( n2 ) );
    assertTrue( n1 != n2 );
  }

  @Test
  public void Should_NotBe_Equal2() {
    var n1 = new PlcI2( WordDeviceCode.W, ADDRESS_2, ( short )10 );

    var n2 = new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short )10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() {
    var n1 = new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short )9 );

    var n2 = new PlcI2( WordDeviceCode.W, ADDRESS_1, ( short )10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_Decode_Value() throws IOException {
//    var val = Coder.decodeValue(
//      getInputStream( new byte[]{ 0x55 } ), DataType.I1 );
//
//    assertEquals( val, ( byte )0x55 );
  }
  //endregion

}
