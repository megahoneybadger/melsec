package melsec.bindings;

import java.text.MessageFormat;

public class PlcObjectPrinter {
  private static String EMPTY_STRING = "";

  public static String toString( IPlcObject o ){
    return toString( o, true );
  }

  public static String toString( IPlcObject o, boolean displayValue ){
    return switch( o.type() ){
      case Bit -> toBitString( ( PlcBit ) o, displayValue );
      case U1, U2, U4, F4, F8 -> toNumericString( ( IPlcNumber ) o, displayValue );

      default -> "plc object";
    };
  }

  private static String toBitString( PlcBit o ){
    return toBitString( o, true );
  }

  private static String toBitString( PlcBit o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var iv = o.value() ? 1 : 0;
    var v = displayValue ? " " + iv : EMPTY_STRING;

    return MessageFormat.format("bit [{0}@{1}{3}]{2}",
      o.device(), o.device().toStringAddress( o.address() ), v, id);
  }

  private static <T extends  Number> String toNumericString( IPlcNumber<T> o ){
    return toNumericString( o, true );
  }

  private static <T extends  Number> String toNumericString( IPlcNumber<T> o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var v = displayValue ? " " + o.value().toString() : EMPTY_STRING;

    return MessageFormat.format("{0} [{1}@{2}{3}]{4}",
      o.type(), o.device(), o.device().toStringAddress( o.address() ), id, v);
  }
}
