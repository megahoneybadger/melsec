package melsec.utils;

import melsec.bindings.IPlcObject;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.StreamSupport;

public class UtilityHelper {
  public static String EMPTY_STRING = "";

  public static <T> List<T> toList(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
      .toList();
  }

  public static  <T> T coalesce( T x, T v ){
    return ( x == null ) ? v : x;
  }

  public static String notNullString( String x ){
    return ( x == null ) ? EMPTY_STRING : x;
  }

  public static String getPlcObjectKey( IPlcObject o ){
    return MessageFormat.format( "{0}{1}", o.device(), o.device().toStringAddress(o.address()) );
  }
}
