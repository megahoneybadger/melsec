package bindings;

import melsec.bindings.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class Strings extends BaseTest {

  @Test
  public void Should_CreateString() throws InvalidDeviceCodeException {
    PlcString s = new PlcString( SIZE_1 );

    assertEquals( s.size(), SIZE_1 );
    assertTrue( s.value().isEmpty() );

    assertEquals( s.device(), DeviceCode.W );
    assertEquals( s.address(), 0 );
    assertEquals( s.localNo(), 1 );
    assertTrue( s.name().isEmpty() );
    assertEquals( s.type(), PlcDataType.String );
  }

  @Test
  public void Should_CreateString2() throws InvalidDeviceCodeException {
    PlcString s = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1 );

    assertEquals( s.device(), DeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertTrue( s.name().isEmpty() );

    assertEquals( s.localNo(), 1 );
    assertTrue( s.name().isEmpty() );
    assertEquals( s.type(), PlcDataType.String );
  }

  @Test
  public void Should_CreateString3() throws InvalidDeviceCodeException {
    PlcString s = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1  );

    assertEquals( s.device(), DeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.value(), STRING_1 );

    assertEquals( s.localNo(), 1 );
    assertTrue( s.name().isEmpty() );
    assertEquals( s.type(), PlcDataType.String );
  }

  @Test
  public void Should_CreateString4() throws InvalidDeviceCodeException {
    PlcString s = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, null );

    assertEquals( s.device(), DeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertTrue( s.value().isEmpty() );

    assertEquals( s.localNo(), 1 );
    assertTrue( s.name().isEmpty() );
  }

  @Test(expected = InvalidDeviceCodeException.class)
  public void ShouldNot_CreateString_WithWrongDevice() throws Exception {
    new PlcString( DeviceCode.B, ADDRESS_1, SIZE_3 );
  }

  public void Should_Be_Equal() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    PlcString s2 = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    assertTrue( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1.toUpperCase() );

    PlcString s2 = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal2() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    PlcString s2 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.W, ADDRESS_2, SIZE_1, STRING_1 );

    PlcString s2 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal4() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    PlcString s2 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_2, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_Be_Equal2() throws InvalidDeviceCodeException {
    PlcString s1 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_1, null );

    PlcString s2 = new PlcString( DeviceCode.W, ADDRESS_1, SIZE_1, "" );

    assertTrue( s1.equals( s2 ) );
  }
}
