package melsec.utils;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.io.IOResponseItem;
import melsec.io.IOType;
import melsec.types.DataType;

import java.text.MessageFormat;

public class Stringer {

  //region Class constants
  /**
   *
   */
  private static String EMPTY_STRING = "";
  //endregion

  //region Class 'toString' methods
  public static String toString( IPlcObject o ){
    return toString( o, true );
  }

  public static String toString( IPlcObject o, boolean displayValue ){
    return switch( o.type() ){
      case Bit -> toBitString( (PlcBit) o, displayValue );
      case U2, U4, I2, I4 -> toNumericString( (IPlcNumber) o, displayValue );
      case String -> toString( (PlcString) o, displayValue );
      case Struct -> toString( (PlcStruct) o, displayValue );

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

  private static String toString( PlcString s ){
    return toString( s, true );
  }

  private static String toString( PlcString o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var v = displayValue ? " " + o.value() : EMPTY_STRING;

    return MessageFormat.format("A{4} [{0}@{1}{2}]{3}",
      o.device(), o.device().toStringAddress( o.address() ), id, v, o.size());
  }

  public static String toString( PlcStruct st ){
    return toString( st, true );
  }

  public static String toString( PlcStruct st, boolean displayValue ){
    var sb = new StringBuilder();

    var id = st.id().isEmpty() ? EMPTY_STRING : " " + st.id();

    sb.append( MessageFormat.format( "struct [{0}@{1}{2}]",
      st.device(), st.device().toStringAddress( st.address() ), id ) );

    if( displayValue ){
      for( var item: st.items() ){
        sb.append( System.lineSeparator() + "\t" );
        sb.append( Stringer.toString( item, displayValue ) );
      }
    }

    return sb.toString();
  }

  public static String toString( Iterable<ICommand> commands)  {
    var sb = new StringBuilder();

    commands.forEach( x -> {
      if( sb.length() > 0 ){
        sb.append( ", " );
      }

      sb.append( x );
    });

    return sb.toString();
  }

  public static String toString( IOResponseItem item ){

    var result = item.result();
    var resPrefix = result.success() ? "OK" : "NG";

    if( item.operation() == IOType.Read ){

      return ( result.success() ) ?

        MessageFormat.format( "Read [{0}] {1}", resPrefix,
          Stringer.toString( result.value() ) ) :

        MessageFormat.format( "Read [{0}] {1} -> {2}", resPrefix,
          Stringer.toString( item.target(), false ), result.error().getMessage() );

    } else {
      return ( result.success() ) ?

        MessageFormat.format( "Write [{0}] {1}", resPrefix,
          Stringer.toString( result.value() ) ) :

        MessageFormat.format( "Write [{0}] {1} -> {2}", resPrefix,
          Stringer.toString( item.target() ), result.error().getMessage() );
    }
  }
  //endregion
}
