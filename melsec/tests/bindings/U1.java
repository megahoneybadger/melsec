package bindings;

import melsec.bindings.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class U1 extends BaseTest {
  @Test
  public void Should_CreateU1()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n = new PlcU1( DeviceCode.W, SIZE_1 );

    assertEquals( n.device(), DeviceCode.W );
    assertEquals( n.address(), SIZE_1 );
    assertEquals( n.localNo(), 1 );
    assertTrue( n.value() == 0 );
    assertTrue( n.name().isEmpty() );
    assertEquals( n.type(), PlcDataType.U1 );
  }

  @Test
  public void Should_CreateU1_2()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n = new PlcU1( DeviceCode.W, SIZE_1, 180 );

    assertEquals( n.device(), DeviceCode.W );
    assertEquals( n.address(), SIZE_1 );
    assertEquals( n.localNo(), 1 );
    assertTrue( n.value() == 180 );
    assertTrue( n.name().isEmpty() );
    assertEquals( n.type(), PlcDataType.U1 );
  }

  @Test(expected = InvalidDeviceCodeException.class)
  public void ShouldNot_CreateU1_WithWrongDevice()
    throws InvalidDeviceCodeException, InvalidNumberException {
    new PlcU1( DeviceCode.B, SIZE_1 );
  }

  @Test(expected = InvalidNumberException.class)
  public void ShouldNot_CreateU1_WithWrongValue()
    throws InvalidDeviceCodeException, InvalidNumberException {
    new PlcU1( DeviceCode.W, SIZE_1, -5 );
  }

  @Test(expected = InvalidNumberException.class)
  public void ShouldNot_CreateU1_WithWrongValue2()
    throws InvalidDeviceCodeException, InvalidNumberException {
    new PlcU1( DeviceCode.W, SIZE_1, 256 );
  }

  @Test
  public void Should_Be_Equal()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n1 = new PlcU1( DeviceCode.W, ADDRESS_2, 10 );

    PlcU1 n2 = new PlcU1( DeviceCode.W, ADDRESS_2, 10 );

    assertTrue( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n1 = new PlcU1( DeviceCode.D, ADDRESS_2, 10 );

    PlcU1 n2 = new PlcU1( DeviceCode.W, ADDRESS_2, 10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal2()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n1 = new PlcU1( DeviceCode.W, ADDRESS_2, 10 );

    PlcU1 n2 = new PlcU1( DeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal3()
    throws InvalidDeviceCodeException, InvalidNumberException {
    PlcU1 n1 = new PlcU1( DeviceCode.W, ADDRESS_1, 9 );

    PlcU1 n2 = new PlcU1( DeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }
}
