package melsec.bindings;

public class Struct extends BaseTest {

//  @Test
//  public void Should_CreateStruct(){
//    var s = PlcStruct
//      .builder()
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.W );
//    assertEquals( s.address(), 0 );
//    assertTrue( s.id().isEmpty() );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithDevice(){
//    var s = PlcStruct
//      .builder()
//      .device( WordDeviceCode.R )
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.R );
//    assertEquals( s.address(), 0 );
//    assertTrue( s.id().isEmpty() );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithAddress(){
//    var s = PlcStruct
//      .builder()
//      .address( ADDRESS_1 )
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.W );
//    assertEquals( s.address(), ADDRESS_1 );
//    assertTrue( s.id().isEmpty() );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithId(){
//    var s = PlcStruct
//      .builder()
//      .id( STRING_1 )
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.W );
//    assertEquals( s.address(), 0 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithNullId(){
//    var s = PlcStruct
//      .builder()
//      .id( null )
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.W );
//    assertEquals( s.address(), 0 );
//    //assertEquals( s.size(), 0 );
//    assertTrue( s.id().isEmpty() );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithTriple(){
//    var s = PlcStruct
//      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
//      .build();
//
//    assertEquals( s.device(), WordDeviceCode.D );
//    assertEquals( s.address(), ADDRESS_1 );
//    //assertEquals( s.size(), 0 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithItems(){
//    var s = PlcStruct
//      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
//      .u1( 100 )
//      .u1( 200 )
//      .u1( 150 )
//      .build();
//
//    var expected = new ArrayList<IPlcObject>();
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1, 100 ) );
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1 + 1, 200 ) );
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1 + 2, 150 ) );
//
//
//    assertEquals( s.device(), WordDeviceCode.D );
//    assertEquals( s.address(), ADDRESS_1 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//
//    //assertEquals( s.size(), 3 );
//    assertEquals( s.count(), 3 );
//    Assertions.assertIterableEquals( s.items(), expected );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithItems2(){
//    var s = PlcStruct
//      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
//      .u1( 10, NAME_1 )
//      .u1( 20, NAME_2 )
//      .u1( 30 )
//      .string( 20, STRING_1, NAME_3 )
//      .string( 10, STRING_2, null )
//      .u2( 125 )
//      .u4( 542365 )
//      .offset( 5 )
//      .u2( 156 )
//      .offset( 2 )
//      .u2( 200 )
//      .build();
//
//    var expected = new ArrayList<IPlcObject>();
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1, 10, NAME_1 ) );
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1 + 1, 20, NAME_2 ) );
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_1 + 2, 30 ) );
//    expected.add( new PlcString( WordDeviceCode.D, ADDRESS_1 + 3, 20, STRING_1, NAME_3 ) );
//    expected.add( new PlcString( WordDeviceCode.D, ADDRESS_1 + 23, 10, STRING_2 ) );
//    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 33, 125 ) );
//    expected.add( new PlcU4( WordDeviceCode.D, ADDRESS_1 + 35, 542365l ) );
//    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 44, 156 ) );
//    expected.add( new PlcU2( WordDeviceCode.D, ADDRESS_1 + 48, 200 ) );
//
//    assertEquals( s.device(), WordDeviceCode.D );
//    assertEquals( s.address(), ADDRESS_1 );
//    //assertEquals( s.size(), 50 );
//    assertEquals( s.count(), 9 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//
//    Assertions.assertIterableEquals( s.items(), expected );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithItemsDeviceReset(){
//    var s = PlcStruct
//      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
//      .u1( 100 )
//      .u1( 200 )
//      .u1( 150 )
//      .device( WordDeviceCode.W )
//      .u1( 10 )
//      .build();
//
//    var expected = new ArrayList<IPlcObject>();
//    expected.add( new PlcU1( WordDeviceCode.W, ADDRESS_1, 10 ) );
//
//    assertEquals( s.device(), WordDeviceCode.W );
//    assertEquals( s.address(), ADDRESS_1 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//
//    //assertEquals( s.size(), 1 );
//    assertEquals( s.count(), 1 );
//    Assertions.assertIterableEquals( s.items(), expected );
//  }
//
//  @Test
//  public void Should_CreateStruct_WithItemsAddressReset(){
//    var s = PlcStruct
//      .builder( WordDeviceCode.D, ADDRESS_1, STRING_1 )
//      .u1( 100 )
//      .u1( 200 )
//      .u1( 150 )
//      .address( ADDRESS_2 )
//      .u1( 20 )
//      .build();
//
//    var expected = new ArrayList<IPlcObject>();
//    expected.add( new PlcU1( WordDeviceCode.D, ADDRESS_2, 20 ) );
//
//    assertEquals( s.device(), WordDeviceCode.D );
//    assertEquals( s.address(), ADDRESS_2 );
//    assertEquals( s.id(), STRING_1 );
//    assertEquals( s.operation(), DataType.Struct );
//
//    //assertEquals( s.size(), 1 );
//    assertEquals( s.count(), 1 );
//    Assertions.assertIterableEquals( s.items(), expected );
//  }

}