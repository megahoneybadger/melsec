package melsec.utils;

import melsec.bindings.IPlcObject;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UtilityHelper {

  //region Class members
  /**
   *
   */
  public static String EMPTY_STRING = "";
  //endregion

  //region Class 'Collection' methods

  public static <T> List<T> toList(final Iterable<T> iterable) {
    return toStream( iterable ).toList();
  }

  public static <T> Stream<T> toStream(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }
  //endregion

  public static  <T> T coalesce( T x, T v ){
    return ( x == null ) ? v : x;
  }

  public static String notNullString( String x ){
    return ( x == null ) ? EMPTY_STRING : x;
  }

  public static String getKey(IPlcObject o ){
    return MessageFormat.format( "{0}@{1}{2}", o.type(), o.device(), o.device().toStringAddress(o.address())  );
  }


}
