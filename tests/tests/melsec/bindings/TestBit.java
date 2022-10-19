package melsec.bindings;


import melsec.types.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestBit extends BaseTest {

  @Test
  public void Should_CreateBit(){
    var bit = new PlcBit();

    assertEquals( bit.device(), BitDeviceCode.B );
    assertEquals( bit.address(), 0 );
    assertTrue( bit.id().isEmpty() );
    assertEquals( bit.type(), DataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithName(){
    var bit = new PlcBit( BitDeviceCode.B, 0, true, NAME_1 );

    assertEquals( bit.device(), BitDeviceCode.B );
    assertEquals( bit.address(), 0 );

    assertEquals( bit.id(), NAME_1 );
    assertTrue( bit.value() );
    assertEquals( bit.type(), DataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithNullName(){
    var bit = new PlcBit( BitDeviceCode.B, 0, true, null );

    assertEquals( bit.device(), BitDeviceCode.B );
    assertEquals( bit.address(), 0 );

    assertTrue( bit.id().isEmpty() );
    assertTrue( bit.value() );
    assertEquals( bit.type(), DataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithNameAndValue() {
    var bit = new PlcBit( BitDeviceCode.B, 0, false, NAME_2 );

    assertEquals( bit.device(), BitDeviceCode.B );
    assertEquals( bit.address(), 0 );
    assertEquals( bit.id(), NAME_2 );
    assertFalse( bit.value() );
    assertEquals( bit.type(), DataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithDeviceAndAddress() {
    var bit = new PlcBit( BitDeviceCode.F, ADDRESS_1 );

    assertEquals( bit.device(), BitDeviceCode.F );
    assertEquals( bit.address(), ADDRESS_1 );
    assertTrue( bit.id().isEmpty() );
    assertFalse( bit.value() );
    assertEquals( bit.type(), DataType.Bit );
  }

  @Test
  public void Should_CreateBit_WithDeviceAndAddressAndValue() {
    var bit = new PlcBit( BitDeviceCode.F, ADDRESS_2, true );

    assertEquals( bit.device(), BitDeviceCode.F );
    assertEquals( bit.address(), ADDRESS_2 );
    assertTrue( bit.id().isEmpty() );
    assertTrue( bit.value() );
    assertEquals( bit.type(), DataType.Bit );
  }


  @Test
  public void Should_Be_Equal() {
    var bit1 = new PlcBit( BitDeviceCode.F, ADDRESS_2, true );

    var bit2 = new PlcBit( BitDeviceCode.F, ADDRESS_2, true );

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal2() {
    var bit1 = new PlcBit();
    var bit2 = new PlcBit();

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal() {
    var bit1 = new PlcBit( BitDeviceCode.B, ADDRESS_1, true );

    var bit2 = new PlcBit( BitDeviceCode.F, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal2() {
    PlcBit bit1 = new PlcBit( BitDeviceCode.B, ADDRESS_1, true );

    PlcBit bit2 = new PlcBit( BitDeviceCode.B, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() {
    var bit1 = new PlcBit( BitDeviceCode.B, ADDRESS_2, false );

    var bit2 = new PlcBit( BitDeviceCode.B, ADDRESS_2, true );

    assertFalse( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal3() {
    var bit1 = new PlcBit( BitDeviceCode.B, ADDRESS_2 );

    var bit2 = new PlcBit( BitDeviceCode.B, ADDRESS_2 );

    assertTrue( bit1.equals( bit2 ) );
  }

  @Test
  public void Should_Be_Equal4() {
    var bit1 = new PlcBit( BitDeviceCode.B, ADDRESS_3, true );

    var bit2 = new PlcBit( BitDeviceCode.B, ADDRESS_3, true );

    assertTrue( bit1.equals( bit2 ) );
  }
}
