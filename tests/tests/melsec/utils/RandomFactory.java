package melsec.utils;

import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.PlcCoordinate;
import melsec.types.WordDeviceCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import static melsec.simulation.Memory.*;
import static melsec.types.DataType.*;

public class RandomFactory {

  //region Class members
  /**
   *
   */
  private static Random random = new Random();
  //endregion

  //region Class 'Numeric' methods

  /**
   * @return
   */
  public static int getU2() {
    return random.nextInt(0, 0xFFFF + 1);
  }
  /**
   * @return
   */
  public static short getI2() {
    return ( short )random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE);
  }
  /**
   * @return
   */
  public static Long getU4() {
    return random.nextLong(0, 0xFFFF_FFFFl + 1 );
  }
  /**
   * @return
   */
  public static int getI4() {
    return random.nextInt( Integer.MIN_VALUE, Integer.MAX_VALUE );
  }
  //endregion

  //region Class 'Plc Object' methods

  /**
   * @return
   */
  public static PlcU2 getPlcU2() {
    return new PlcU2(getWordDeviceCode(), getAddress(), getU2());
  }

  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcU2(int count) {
    return getPlcObjects( count, () -> getPlcU2() );
  }

  /**
   * @return
   */
  public static PlcI2 getPlcI2() {
    return new PlcI2(getWordDeviceCode(), getAddress(), getI2());
  }

  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcI2(int count) {
    return getPlcObjects( count, () -> getPlcI2() );
  }

  /**
   * @return
   */
  public static PlcU4 getPlcU4() {
    var address = getAddress();

    if( address == MAX_WORDS - 1 )address--;

    if( address % 2 != 0 )address++;

    return new PlcU4( getWordDeviceCode(), address, getU4());
  }

  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcU4(int count) {
    return getPlcObjects( count, () -> getPlcU4() );
  }

  /**
   * @return
   */
  public static PlcI4 getPlcI4() {
    var address = getAddress();

    if( address == MAX_WORDS - 1 )address--;

    if( address % 2 != 0 )address++;

    return new PlcI4(getWordDeviceCode(), address, getI4());
  }

  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcI4(int count) {
    return getPlcObjects( count, () -> getPlcI4() );
  }

  /**
   *
   * @return
   */
  public static int getAddress(){
    return random.nextInt(0, MAX_WORDS );
  }

  public static List<IPlcObject> getPlcNumerics( int count ){
    DataType[] types = {
      I2, U2, I4, U4
    };

    var list = new ArrayList<IPlcObject>();
    var checker = new CoordinateIntersectionChecker();

    for(int i = 0; i < count; ++i) {
      IPlcObject next = null;

      var index = random.nextInt( 0, types.length );
      var type = types[ index ];

      do{
        next = switch( type ){
          case I2 -> getPlcI2();
          case I4 -> getPlcI4();
          case U2 -> getPlcU2();
          case U4 -> getPlcU4();
          default -> null;
        };

      }
      while( !checker.accept( ( IPlcWord ) next ) );

      list.add( next );
    }

    return list;
  }

  /**
   * @param count
   * @return
   */
  private static <T extends IPlcObject> List<T> getPlcObjects(int count, Callable<T> func ) {
    var list = new ArrayList<T>();
    var checker = new CoordinateIntersectionChecker();

    for(int i = 0; i < count; ++i) {
      T next = null;

      do{
        try{
          next = func.call();
        }
        catch( Exception e ){

        }

      }
      while( !checker.accept( ( IPlcWord ) next ) );

      list.add( next );
    }

    return list;
  }

  /**
   * @return
   */
  public static WordDeviceCode getWordDeviceCode() {
    var values = WordDeviceCode.values();
    var index = random.nextInt(0, values.length);
    return values[index];
  }

  /**
   * @return
   */
  public static BitDeviceCode getBitDeviceCode() {
    var values = BitDeviceCode.values();
    var index = random.nextInt(0, values.length);
    return values[index];
  }
  //endregion

  //region Class internal structs
  public static class CoordinateIntersectionChecker{
    private HashSet<PlcCoordinate> set = new HashSet<>();

    public boolean accept( IPlcWord w ){
      var points = ByteConverter.getPointsCount( w );
      var coord = UtilityHelper.getCoordinate( w );

      for( int i = 0; i < points; ++i ){
        if( set.contains( coord ) )
          return false;

        coord = coord.shiftRight();
      }

      coord = UtilityHelper.getCoordinate( w );

      for( int i = 0; i < points; ++i ){
        set.add( coord );

        coord = coord.shiftRight();
      }

      return true;
    }
  }
  //endregion
}

