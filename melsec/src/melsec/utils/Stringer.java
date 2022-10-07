package melsec.utils;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.types.IDeviceCode;
import melsec.types.PlcCoordinate;
import melsec.types.PlcRegion;
import melsec.types.io.IORequestItem;
import melsec.types.io.IOResponseItem;
import melsec.types.io.IOType;

import java.text.MessageFormat;
import java.util.List;

public class Stringer {

  //region Class constants
  /**
   *
   */
  private static String EMPTY_STRING = "";
  //endregion

  //region Class 'toString' methods
  /**
   *
   * @param code
   * @return
   */
  public static String toString( IDeviceCode code ){
    return toString( code, true );
  }
  /**
   *
   * @param code
   * @return
   */
  public static String toString( IDeviceCode code, boolean includeType ){
    var d = (( Enum )code).name();

    if( includeType && !code.isDecimalAddress() ){
      d += "x";
    }

    return d;
  }
  /**
   *
   * @param list
   * @param displayValue
   * @return
   */
  public static String toString( List<IPlcObject> list, boolean displayValue ){
    var sb = new StringBuilder();

    for( var l: list ){
      if( sb.length() > 0 ){
        sb.append( ", " );
      }

      sb.append( Stringer.toString( l, displayValue ) );
    }

    return sb.toString();
  }
  /**
   *
   * @param o
   * @return
   */
  public static String toString( IPlcObject o ){
    return toString( o, true );
  }
  /**
   *
   * @param o
   * @param displayValue
   * @return
   */
  public static String toString( IPlcObject o, boolean displayValue ){
    return switch( o.type() ){
      case Bit -> toBitString( (PlcBit) o, displayValue );
      case U2, U4, I2, I4 -> toNumericString( (IPlcNumber) o, displayValue );
      case String -> toString( (PlcString) o, displayValue );
      case Struct -> toString( (PlcStruct) o, displayValue );
      case Binary -> toString( ( PlcBinary ) o);

      default -> "plc object";
    };
  }
  /**
   *
   * @param o
   * @return
   */
  private static String toBitString( PlcBit o ){
    return toBitString( o, true );
  }

  private static String toBitString( PlcBit o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var iv = o.value() ? 1 : 0;
    var v = displayValue ? " " + iv : EMPTY_STRING;

    return MessageFormat.format("bit [{0}{1}{3}]{2}",
      toString( o.device() ), o.device().toStringAddress( o.address() ), v, id);
  }

  private static <T extends  Number> String toNumericString( IPlcNumber<T> o ){
    return toNumericString( o, true );
  }

  private static <T extends  Number> String toNumericString( IPlcNumber<T> o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var v = displayValue && o.value().intValue() != 0 ?
      " " + o.value().toString() : EMPTY_STRING;

    return MessageFormat.format("{0} [{1}{2}{3}]{4}",
      o.type(), toString( o.device() ), o.device().toStringAddress( o.address() ), id, v);
  }

  private static String toString( PlcString s ){
    return toString( s, true );
  }

  private static String toString( PlcString o, boolean displayValue ){
    var id = o.id().isEmpty() ? EMPTY_STRING : " " + o.id();

    var v = displayValue && !o.value().isEmpty() ? " " + o.value() : EMPTY_STRING;

    return MessageFormat.format("A{4} [{0}{1}{2}]{3}",
      toString( o.device() ), o.device().toStringAddress( o.address() ),
      id, v, Integer.toString( o.size() ));
  }

  public static String toString( PlcBinary r ){
    var value = "";

    if( r.value() != null &&  r.value().length > 0 ){
      var sb = new StringBuilder();
      var len = Math.min( 10, r.value().length );

      for( int i = 0; i < len; ++i ){
        if( sb.length() > 0 ) {
          sb.append( " " );
        }

        sb.append( String.format( "%02X", r.value()[ i ] ) );
      }

      if( r.value().length > 10 ){
        sb.append( " ..." );
      }

      value = MessageFormat.format( " {0}", sb.toString() );
    }

    return MessageFormat.format("binary {0}{1}",
      toRangeString( r ), value );
  }

  public static String toRangeString( PlcBinary r ){
    var from = r
      .device()
      .toStringAddress( r.address() );

    var to = r
      .device()
      .toStringAddress( r.address() + r.size() - 1 );

    if( 0 == r.size() ){
      to = from;
    }

    var device = Stringer.toString( r.device() );

    return MessageFormat.format( "[{0}{1}-{0}{2}]", device, from, to );
  }

  /**
   *
   * @param list
   * @return
   */
  public static String toString( List<PlcRegion> list ){
    var sb = new StringBuilder();

    for( var l: list ){
      if( sb.length() > 0 ){
        sb.append( ", " );
      }

      sb.append( Stringer.toString( l ) );
    }

    return sb.toString();
  }
  /**
   *
   * @param r
   * @return
   */
  public static String toString( PlcRegion r ){

    var from = r
      .device()
      .toStringAddress( r.start() );

    var to = r
      .device()
      .toStringAddress( r.start() + r.size() - 1 );

    if( 0 == r.size() ){
      to = from;
    }

    var device = Stringer.toString( r.device() );

    return MessageFormat.format( "[{0}{1}-{0}{2}]", device, from, to );
  }

  public static String toString( PlcStruct st ){
    return toString( st, true );
  }

  public static String toString( PlcStruct st, boolean displayValue ){
    var sb = new StringBuilder();

    var id = st.id().isEmpty() ? EMPTY_STRING : " " + st.id();

    sb.append( MessageFormat.format( "struct [{0}{1}{2}]",
      toString( st.device() ), st.device().toStringAddress( st.address() ), id ) );

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
          Stringer.toString( result.value(), true ) ) :

        MessageFormat.format( "Read [{0}] {1} -> {2}", resPrefix,
          Stringer.toString( item.proto(), false ),
          UtilityHelper.coalesce( result.error().getMessage(), result.error().toString() ) );

    } else {
      return ( result.success() ) ?

        MessageFormat.format( "Write [{0}] {1}", resPrefix,
          Stringer.toString( result.value() ) ) :

        MessageFormat.format( "Write [{0}] {1} -> {2}", resPrefix,
          Stringer.toString( item.proto() ),
          UtilityHelper.coalesce( result.error().getMessage(), result.error().toString() ) );
    }
  }

  /**
   *
   * @param item
   * @return
   */
  public static String toString( IORequestItem item ){
    return MessageFormat.format(
      "{0} {1}", item.type(), Stringer.toString( item.object(), false ) );
  }

  /**
   *
   * @param c
   * @return
   */
  public static String toString( PlcCoordinate c ){
    return MessageFormat.format( "{0}{1}",
      toString( c.device(), true ), c.device().toStringAddress( c.address() ) );
  }


  //endregion
}
