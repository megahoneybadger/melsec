package melsec.bindings;

import melsec.utils.ByteConverter;
import melsec.utils.Copier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import melsec.types.*;

import java.util.ArrayList;

public class TestStruct extends BaseTest {

  @Test
  public void Should_CreateStruct() {
    var s = PlcStruct
      .builder()
      .build();

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), 0 );
    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithDevice() {
    var s = PlcStruct
      .builder()
      .device( WordDeviceCode.R )
      .build();

    assertEquals( s.device(), WordDeviceCode.R );
    assertEquals( s.address(), 0 );
    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithAddress() {
    var s = PlcStruct
      .builder()
      .address( ADDRESS_1 )
      .build();

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), ADDRESS_1 );
    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithId() {
    var s = PlcStruct
      .builder()
      .id( STRING_1 )
      .build();

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), 0 );
    assertEquals( s.id(), STRING_1 );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithNullId(){
    var s = PlcStruct
      .builder()
      .id( null )
      .build();

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), 0 );
    assertTrue( s.id().isEmpty() );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithTriple(){
    var s = PlcStruct
      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
      .build();

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.id(), STRING_1 );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 0 );
  }

  @Test
  public void Should_CreateStruct_WithItems(){
    var s = PlcStruct
      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
      .u2( 100 )
      .u2( 200 )
      .u2( 150 )
      .build();

    var expected = new ArrayList<IPlcObject>();
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1, 100 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 1, 200 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 2, 150 ) );


    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.id(), STRING_1 );
    assertEquals( s.count(), 3 );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 3 );

    Assertions.assertIterableEquals( s.items(), expected );
  }

  @Test
  public void Should_CreateStruct_WithItems2(){
    var s = PlcStruct
      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
      .u2( 10, NAME_1 )
      .u2( 20, NAME_2 )
      .u2( 30 )
      .string( 20, STRING_1, NAME_3 )
      .string( 10, STRING_2, null )
      .u2( 125 )
      .u4( 542365 )
      .offset( 5 )
      .u2( 156 )
      .offset( 2 )
      .u2( 200 )
      .build();

    var expected = new ArrayList<IPlcObject>();
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1, 10, NAME_1 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 1, 20, NAME_2 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 2, 30 ) );
    expected.add( new PlcString( WordDeviceCode.D, ADDRESS_1 + 3, 20, STRING_1, NAME_3 ) );
    expected.add( new PlcString( WordDeviceCode.D, ADDRESS_1 + 13, 10, STRING_2 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 18, 125 ) );
    expected.add( new PlcU4( WordDeviceCode.D, ADDRESS_1 + 19, 542365l ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 26, 156 ) );
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 29, 200 ) );

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.count(), 9 );
    assertEquals( s.id(), STRING_1 );
    assertEquals( s.type(), DataType.Struct );
    assertEquals( ByteConverter.getPointsCount( s ), 30 );

    Assertions.assertIterableEquals( s.items(), expected );
  }

  @Test
  public void Should_CreateStruct_WithItemsDeviceReset(){
    var s = PlcStruct
      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
      .u2( 100 )
      .u2( 200 )
      .u2( 150 )
      .device( WordDeviceCode.W )
      .u2( 10 )
      .build();

    var expected = new ArrayList<IPlcObject>();
    expected.add( new PlcU2( WordDeviceCode.W, ADDRESS_1, 10 ) );

    assertEquals( s.device(), WordDeviceCode.W );
    assertEquals( s.address(), ADDRESS_1 );
    assertEquals( s.id(), STRING_1 );

    assertEquals( ByteConverter.getPointsCount( s ), 1 );
    assertEquals( s.count(), 1 );
    Assertions.assertIterableEquals( s.items(), expected );
  }

  @Test
  public void Should_CreateStruct_WithItemsAddressReset(){
    var s = PlcStruct
      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
      .u2( 100 )
      .u2( 200 )
      .u2( 150 )
      .address( ADDRESS_2 )
      .u2( 20 )
      .build();

    var expected = new ArrayList<IPlcObject>();
    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_2, 20 ) );

    assertEquals( s.device(), WordDeviceCode.D );
    assertEquals( s.address(), ADDRESS_2 );
    assertEquals( s.id(), STRING_1 );

    assertEquals( s.count(), 1 );
    assertEquals( ByteConverter.getPointsCount( s ), 1 );
    Assertions.assertIterableEquals( s.items(), expected );
  }

}