package melsec.utils;

import melsec.bindings.IPlcObject;
import melsec.types.PlcCoordinate;
import melsec.types.PlcKey;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
  /**
   *
   * @param protos
   * @return
   */
  public static List<IPlcObject> union( Iterable<IPlcObject> ... protos ){
    return Arrays
      .stream( protos )
      .flatMap( x -> UtilityHelper.toStream( x ) )
      .toList();
  }

  /**
   *
   * @param protos
   * @return
   */
  public static List<IPlcObject> unionShuffle( Iterable<IPlcObject> ... protos ){
    var list = Arrays
      .stream( protos )
      .flatMap( x -> UtilityHelper.toStream( x ) )
      .toList();

    var res = new ArrayList<>( list );

    Collections.shuffle( res );

    return res;
  }
  //endregion

  //region Class utility methods

  /**
   *
   * @param x
   * @param v
   * @return
   * @param <T>
   */
  public static <T> T coalesce( T x, T v ){
    return ( x == null ) ? v : x;
  }

  /**
   *
   * @param x
   * @return
   */
  public static String notNullString( String x ){
    return ( x == null ) ? EMPTY_STRING : x;
  }
  //endregion

  //region Class 'PLC Identification' methods

  /**
   *
   * @param o
   * @return
   */
  public static PlcCoordinate getCoordinate( IPlcObject o ){
    return new PlcCoordinate( o.device(), o.address() );
  }

  /**
   *
   * @param o
   * @return
   */
  public static PlcKey getKey( IPlcObject o ){
    return new PlcKey( o.type(), o.device(), o.address() );
  }
  //endregion
}
