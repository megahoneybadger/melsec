package melsec.bindings;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import melsec.types.*;

public class U1 extends BaseTest {
  @Test
  public void Should_CreateU1(){
    var n = new PlcU1( WordDeviceCode.W, ADDRESS_1 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == 0 );
    assertTrue( n.size() == 1 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U1 );
  }

  @Test
  public void Should_CreateU1_2() {
    var n = new PlcU1( WordDeviceCode.W, ADDRESS_1, 180 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == 180 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U1 );
  }

  @Test
  public void Should_Create_WithOverflow() {
    var n = new PlcU1( WordDeviceCode.W, ADDRESS_1, 280 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == PlcU1.MAX_VALUE );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U1 );
  }

  @Test
  public void Should_Create_WithUnderflow() {
    var n = new PlcU1( WordDeviceCode.W, ADDRESS_1, -10, NAME_2 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == PlcU1.MIN_VALUE );
    assertEquals( n.id(), NAME_2 );
    assertEquals( n.type(), DataType.U1 );
  }

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
