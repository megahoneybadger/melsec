package melsec.bindings;

import org.junit.Test;
import types.DataType;
import types.WordDeviceCode;

import static org.junit.Assert.*;

public class Strings extends BaseTest {

  @Test
  public void Should_CreateString() {
    var s = new PlcString( SIZE_1 );

    assertEquals( s.size(), SIZE_1 );
    assertTrue( s.value().isEmpty() );

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), 0 );
    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.String );
  }

  @Test
  public void Should_CreateString2() {
    var s = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1 );

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertTrue( s.id().isEmpty() );

    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.String );
  }

  @Test
  public void Should_CreateString3() {
    var s = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.value(), STRING_1 );

    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.String );
  }

  @Test
  public void Should_CreateString4() {
    var s = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, null );

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.size(), SIZE_1 );
    assertEquals( s.address(), ADDRESS_1 );
    assertTrue( s.value().isEmpty() );

    assertTrue( s.id().isEmpty() );
  }

  @Test
  public void Should_Be_Equal() {
    var s1 = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    var s2 = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    assertTrue( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal() {
    var s1 = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1.toUpperCase() );

    var s2 = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal2() {
    var s1 = new PlcString( WordDeviceCode.D, ADDRESS_1, SIZE_1, STRING_1 );

    var s2 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() {
    var s1 = new PlcString( WordDeviceCode.W, ADDRESS_2, SIZE_1, STRING_1 );

    var s2 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_NotBe_Equal4() {
    var s1 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, STRING_1 );

    var s2 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_2, STRING_1 );

    assertFalse( s1.equals( s2 ) );
  }

  @Test
  public void Should_Be_Equal2() {
    var s1 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, null );

    var s2 = new PlcString( WordDeviceCode.W, ADDRESS_1, SIZE_1, "" );

    assertTrue( s1.equals( s2 ) );
  }
}
