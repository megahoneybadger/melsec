package melsec.utils;

import melsec.bindings.*;
import melsec.types.WordDeviceCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Valuer {

  //region Class public methods
  /**
   *
   * @param proto
   * @return
   */
  public static Object getValue( IPlcObject proto ){
    return switch( proto.type() ){
      case Bit -> (( PlcBit ) proto).value();
      case U2 -> (( PlcU2 ) proto).value();
      case U4 -> (( PlcU4 ) proto).value();

      case I2 -> (( PlcI2 ) proto).value();
      case I4 -> (( PlcI4 ) proto).value();
      case String -> (( PlcString ) proto ).value();
      case Struct -> (( PlcStruct ) proto ).items();
      case Binary -> (( PlcBinary ) proto ).value();
    };
  }
  /**
   *
   * @param v
   * @param o
   * @return
   */
  public static boolean equals( Object v, IPlcObject o ){
    return v.equals( getValue( o ) );
  }
  //endregion

}
