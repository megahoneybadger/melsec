package melsec.utils;

import melsec.bindings.*;

import java.util.Arrays;
import java.util.List;

public class Copier {

  //region Class 'With' methods
  /**
   *
   * @param proto
   * @param value
   * @return
   */
  public static IPlcObject with( IPlcObject proto, Object value ){
    return switch( proto.type() ){
      case Bit -> (( PlcBit ) proto ).with( ( boolean )value );

      case U2 -> (( PlcU2 ) proto ).with( ( int )value );
      case U4 -> (( PlcU4 ) proto ).with( ( long )value );

      case I2 -> (( PlcI2 ) proto ).with( ( short )value );
      case I4 -> (( PlcI4 ) proto ).with( ( int )value );

      case String ->(( PlcString ) proto ).with( ( String )value );

      case Struct -> {
        var st = ( PlcStruct ) proto;
        yield PlcStruct.builder().with( st, ( List<IPlcWord> )value );
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
  public static PlcStruct with( PlcStruct p, List<IPlcWord> items ){
    return PlcStruct
      .builder()
      .with( p, items );
  }
  //endregion

  //region Class 'Without' methods
  /**
   *
   * @param proto
   * @return
   */
  public static IPlcObject without( IPlcObject proto ){
    return switch( proto.type() ){
      case Bit -> (( PlcBit ) proto ).without();

      case U2 -> (( PlcU2 ) proto ).without();
      case U4 -> (( PlcU4 ) proto ).without();

      case I2 -> (( PlcI2 ) proto ).without();
      case I4 -> (( PlcI4 ) proto ).without();

      case String ->(( PlcString ) proto ).without();
//
//      case Struct -> {
//        var st = ( PlcStruct ) proto;
//        yield PlcStruct.builder().with( st, ( List<IPlcWord> )value );
//      }

      default -> null;
    };
  }
  /**
   *
   * @param proto
   * @return
   */
  public static List<IPlcObject> without( IPlcObject [] proto ){
    return without( Arrays.stream( proto ).toList() );
  }
  /**
   *
   * @param proto
   * @return
   */
  public static List<IPlcObject> without( Iterable<IPlcObject> proto ){
    return UtilityHelper
      .toStream( proto )
      .map( x -> without( x ) )
      .toList();
  }
  //endregion
}
