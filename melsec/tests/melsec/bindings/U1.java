package melsec.bindings;

import org.junit.Test;
import types.DataType;
import types.WordDeviceCode;

import static org.junit.Assert.*;

public class U1 extends BaseTest {
  @Test
  public void Should_CreateU1(){
    var n = new PlcU1( WordDeviceCode.W, SIZE_1 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), SIZE_1 );
    assertTrue( n.value() == 0 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U1 );
  }

  @Test
  public void Should_CreateU1_2() {
    var n = new PlcU1( WordDeviceCode.W, SIZE_1, 180 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), SIZE_1 );
    assertTrue( n.value() == 180 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U1 );
  }

//
//  @Test(expected = InvalidNumberException.class)
//  public void ShouldNot_CreateU1_WithWrongValue() {
//    new PlcU1( WordDeviceCode.W, SIZE_1, -5 );
//  }
//
//  @Test(expected = InvalidNumberException.class)
//  public void ShouldNot_CreateU1_WithWrongValue2() {
//    new PlcU1( WordDeviceCode.W, SIZE_1, 256 );
//  }
//
  @Test
  public void Should_Be_Equal(){
    var n1 = new PlcU1( WordDeviceCode.W, ADDRESS_2, 10 );

    var n2 = new PlcU1( WordDeviceCode.W, ADDRESS_2, 10 );

    assertTrue( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal() {
    var n1 = new PlcU1( WordDeviceCode.D, ADDRESS_2, 10 );

    var n2 = new PlcU1( WordDeviceCode.W, ADDRESS_2, 10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal2() {
    var n1 = new PlcU1( WordDeviceCode.W, ADDRESS_2, 10 );

    var n2 = new PlcU1( WordDeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() {
    var n1 = new PlcU1( WordDeviceCode.W, ADDRESS_1, 9 );

    var n2 = new PlcU1( WordDeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }
}
