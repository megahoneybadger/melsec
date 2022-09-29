package melsec.utils;

import melsec.bindings.*;
import melsec.types.WordDeviceCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Copier {

  //region Class public methods
  /**
   *
   * @param proto
   * @param value
   * @return
   */
  public static IPlcObject withValue( IPlcObject proto, Object value ){
    return switch( proto.type() ){
      case Bit -> {
        var t = (( PlcBit ) proto);
        yield new PlcBit( t.device(), t.address(), ( boolean )value , t.id() );
      }
      case U2 -> {
        var t = (( PlcU2 ) proto);
        yield new PlcU2( t.device(), t.address(), ( int )value, t.id() );
      }
      case U4 -> {
        var t = (( PlcU4 ) proto);
        yield new PlcU4( t.device(), t.address(), ( long )value, t.id() );
      }
      case I2 -> {
        var t = (( PlcI2 ) proto);
        yield new PlcI2( t.device(), t.address(), ( short )value, t.id() );
      }
      case I4 -> {
        var t = (( PlcI4 ) proto);
        yield new PlcI4( t.device(), t.address(), ( int )value, t.id() );
      }
      case String -> {
        var t = (( PlcString ) proto );
        yield new PlcString( t.device(), t.address(), t.size(), ( String )value, t.id() );
      }
      case Struct -> {
        var t = (( PlcStruct ) proto );
        yield PlcStruct.builder().with( t, ( List<IPlcWord> )value );
      }
      case Binary -> {
        var t = (( PlcBinary ) proto );
        yield new PlcBinary( t.device(), t.address(), t.size(), ( byte[] )value, t.id() );
      }

      default -> null;
    };
  }
  /**
   *
   * @param p
   * @param items
   * @return
   */
  public static PlcStruct withValue( PlcStruct p, List<IPlcWord> items ){
    return PlcStruct
      .builder()
      .with( p, items );
  }
  /**
   *
   * @param proto
   * @param address
   * @return
   */
  public static IPlcObject withAddress( IPlcObject proto, int address ){
    return switch( proto.type() ){
      case Bit -> {
        var t = (( PlcBit ) proto);
        yield new PlcBit( t.device(), address, t.value(), t.id() );
      }
      case U2 -> {
        var t = (( PlcU2 ) proto);
        yield new PlcU2( t.device(), address, t.value(), t.id() );
      }
      case U4 -> {
        var t = (( PlcU4 ) proto);
        yield new PlcU4( t.device(), address, t.value(), t.id() );
      }
      case I2 -> {
        var t = (( PlcI2 ) proto);
        yield new PlcI2( t.device(), address, t.value(), t.id() );
      }
      case I4 -> {
        var t = (( PlcI4 ) proto);
        yield new PlcI4( t.device(), address, t.value(), t.id() );
      }
      case String -> {
        var t = (( PlcString ) proto );
        yield new PlcString( t.device(), address, t.size(), t.value(), t.id() );
      }
      case Struct -> withAddress( ( PlcStruct ) proto, address );

      case Binary -> {
        var t = (( PlcBinary ) proto );
        yield new PlcBinary( t.device(), address, t.size(), t.value(), t.id() );
      }

      default -> null;
    };
  }
  /**
   *
   * @param proto
   * @param address
   * @return
   */
  private static PlcStruct withAddress( PlcStruct proto, int address ){
    var items = new ArrayList<IPlcWord>();

    var shift = address - proto.address();

    for( var item : proto.items() ){
      items.add( ( IPlcWord ) withAddress( item, item.address() + shift ) );
    }

    var newBase = PlcStruct
      .builder( ( WordDeviceCode ) proto.device(), address )
      .build();

    return PlcStruct.builder().with( newBase, items );
  }
  /**
   *
   * @param proto
   * @return
   */
  public static IPlcObject withoutValue( IPlcObject proto ){
    return switch( proto.type() ){
      case Bit -> {
        var t = (( PlcBit ) proto);
        yield new PlcBit( t.device(), t.address(), false , t.id() );
      }
      case U2 -> {
        var t = (( PlcU2 ) proto);
        yield new PlcU2( t.device(), t.address(), 0, t.id() );
      }
      case U4 -> {
        var t = (( PlcU4 ) proto);
        yield new PlcU4( t.device(), t.address(), 0l, t.id() );
      }
      case I2 -> {
        var t = (( PlcI2 ) proto);
        yield new PlcI2( t.device(), t.address(), ( short )0, t.id() );
      }
      case I4 -> {
        var t = (( PlcI4 ) proto);
        yield new PlcI4( t.device(), t.address(), 0, t.id() );
      }
      case String -> {
        var t = (( PlcString ) proto );
        yield new PlcString( t.device(), t.address(), t.size(), null, t.id() );
      }
      case Struct -> PlcStruct.builder().without( (( PlcStruct ) proto ) );

      case Binary -> {
        var t = (( PlcBinary ) proto );
        yield new PlcBinary( t.device(), t.address(), t.size(), null, t.id() );
      }


      default -> null;
    };
  }
  /**
   *
   * @param proto
   * @return
   */
  public static List<IPlcObject> withoutValue( IPlcObject [] proto ){
    return withoutValue( Arrays.stream( proto ).toList() );
  }
  /**
   *
   * @param proto
   * @return
   */
  public static List<IPlcObject> withoutValue( Iterable<IPlcObject> proto ){
    return UtilityHelper
      .toStream( proto )
      .map( x -> withoutValue( x ) )
      .toList();
  }
  //endregion

}
