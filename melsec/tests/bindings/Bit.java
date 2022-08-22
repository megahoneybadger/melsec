package bindings;

import org.junit.Test;
import static org.junit.Assert.*;

import melsec.bindings.*;

public class Bit extends BaseTest {

  @Test
  public void Should_CreateBit() throws InvalidDeviceCodeException {
    PlcBit bit = new PlcBit();

    assertEquals( bit.device(), DeviceCode.B );
    assertEquals( bit.address(), 0 );
    assertEquals( bit.localNo(), 1 );
    assertTrue( bit.name().isEmpty() );
    assertEquals( bit.type(), PlcDataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithName() throws InvalidDeviceCodeException {
    PlcBit bit = new PlcBit( NAME_1, true );

    assertEquals( bit.device(), DeviceCode.B );
    assertEquals( bit.address(), 0 );
    assertEquals( bit.localNo(), 1 );
    assertEquals( bit.name(), NAME_1 );
    assertTrue( bit.value() );
    assertEquals( bit.type(), PlcDataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithNameAndValue() throws InvalidDeviceCodeException {
    PlcBit bit = new PlcBit( NAME_2, false );

    assertEquals( bit.device(), DeviceCode.B );
    assertEquals( bit.address(), 0 );
    assertEquals( bit.localNo(), 1 );
    assertEquals( bit.name(), NAME_2 );
    assertFalse( bit.value() );
    assertEquals( bit.type(), PlcDataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithDeviceAndAddress() throws InvalidDeviceCodeException {
    PlcBit bit = new PlcBit( DeviceCode.F, ADDRESS_1 );

    assertEquals( bit.device(), DeviceCode.F );
    assertEquals( bit.address(), ADDRESS_1 );
    assertEquals( bit.localNo(), 1 );
    assertTrue( bit.name().isEmpty() );
    assertFalse( bit.value() );
    assertEquals( bit.type(), PlcDataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithDeviceAndAddressAndValue() throws InvalidDeviceCodeException {
    PlcBit bit = new PlcBit( DeviceCode.F, ADDRESS_2, true );

    assertEquals( bit.device(), DeviceCode.F );
    assertEquals( bit.address(), ADDRESS_2 );
    assertEquals( bit.localNo(), 1 );
    assertTrue( bit.name().isEmpty() );
    assertTrue( bit.value() );
    assertEquals( bit.type(), PlcDataType.Bit );
  }

  @Test(expected = InvalidDeviceCodeException.class)
  public void ShouldNot_CreateBit_WithWrongDevice() throws Exception {
    PlcBit bit = new PlcBit( DeviceCode.W, ADDRESS_1 );
  }

  @Test
  public void Should_Be_Equal() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.F, ADDRESS_2, true );

    PlcBit bit2 = new PlcBit( DeviceCode.F, ADDRESS_2, true );

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal2() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit();
    PlcBit bit2 = new PlcBit();

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.B, ADDRESS_1, true );

    PlcBit bit2 = new PlcBit( DeviceCode.F, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal2() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.B, ADDRESS_1, true );

    PlcBit bit2 = new PlcBit( DeviceCode.B, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.B, ADDRESS_2, false );

    PlcBit bit2 = new PlcBit( DeviceCode.B, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal3() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.B, ADDRESS_2 );

    PlcBit bit2 = new PlcBit( DeviceCode.B, ADDRESS_2 );

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal4() throws InvalidDeviceCodeException {
    PlcBit bit1 = new PlcBit( DeviceCode.B, ADDRESS_3, true );

    PlcBit bit2 = new PlcBit( DeviceCode.B, ADDRESS_3, true );

    assertTrue( bit1.equals( bit2 ) );
  }
}
