package melsec.bindings;

import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.utils.Copier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlcU2Test extends BaseTest {

  //region Class 'Test' methods
  @Test
  public void Should_CreateU2(){
    var n = new PlcU2( WordDeviceCode.W, ADDRESS_1 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == 0 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U2 );
  }

  @Test
  public void Should_CreateU2_2() {
    var n = new PlcU2( WordDeviceCode.W, ADDRESS_1, 180 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == 180 );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U2 );
  }

  @Test
  public void Should_Create_WithOverflow() {
    var n = new PlcU2( WordDeviceCode.W, ADDRESS_1, 66875 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == PlcU2.MAX_VALUE );
    assertTrue( n.id().isEmpty() );
    assertEquals( n.type(), DataType.U2 );
  }

  @Test
  public void Should_Create_WithUnderflow() {
    var n = new PlcU2( WordDeviceCode.W, ADDRESS_1, -10, NAME_2 );

    assertEquals( n.device(), WordDeviceCode.W );
    assertEquals( n.address(), ADDRESS_1 );
    assertTrue( n.value() == PlcU2.MIN_VALUE );
    assertEquals( n.id(), NAME_2 );
    assertEquals( n.type(), DataType.U2 );
  }

  @Test
  public void Should_Be_Equal(){
    var n1 = new PlcU2( WordDeviceCode.W, ADDRESS_2, 10 );

    var n2 = new PlcU2( WordDeviceCode.W, ADDRESS_2, 10 );

    assertTrue( n1.equals( n2 ) );
  }

  @Test
  public void Should_Be_Equal2() {
    var n1 = new PlcU2( WordDeviceCode.D, ADDRESS_2, 10 );

    var n2 = Copier.withValue( n1, 10 );

    assertTrue( n1.equals( n2 ) );
    assertTrue( n1 != n2 );
  }

  @Test
  public void Should_NotBe_Equal() {
    var n1 = new PlcU2( WordDeviceCode.D, ADDRESS_2, 10 );

    var n2 = new PlcU2( WordDeviceCode.W, ADDRESS_2, 10 );

    assertFalse( n1.equals( n2 ) );
  }



  @Test
  public void Should_NotBe_Equal2() {
    var n1 = new PlcU2( WordDeviceCode.W, ADDRESS_2, 10 );

    var n2 = new PlcU2( WordDeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }

  @Test
  public void Should_NotBe_Equal3() {
    var n1 = new PlcU2( WordDeviceCode.W, ADDRESS_1, 9 );

    var n2 = new PlcU2( WordDeviceCode.W, ADDRESS_1, 10 );

    assertFalse( n1.equals( n2 ) );
  }
  //endregion

}
