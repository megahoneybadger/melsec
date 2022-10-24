package melsec.utils;

import jdk.jshell.execution.Util;
import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.PlcCoordinate;
import melsec.types.WordDeviceCode;

import java.util.*;
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
  /**
   *
   * @return
   */
  public static boolean getBit() {
    return random.nextBoolean();
  }
  /**
   *
   * @return
   */
  public static String getString() {
    return getString( 10 );
  }
  /**
   *
   * @param size
   * @return
   */
  public static String getString(int size) {
    if( size == 0 )
      return UtilityHelper.EMPTY_STRING;

    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'

    var generatedString = random.ints(leftLimit, rightLimit + 1)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(size)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();

    return generatedString;
  }
  //endregion

  //region Class 'Plc Object' methods
  /**
   * @return
   */
  public static PlcU2 getPlcU2() {
    return new PlcU2(getWordDeviceCode(), getWordAddress(), getU2());
  }
  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcU2(int count) {
    return getWords( count, () -> getPlcU2() );
  }
  /**
   * @return
   */
  public static PlcI2 getPlcI2() {
    return new PlcI2(getWordDeviceCode(), getWordAddress(), getI2());
  }
  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcI2(int count) {
    return getWords( count, () -> getPlcI2() );
  }
  /**
   * @return
   */
  public static PlcU4 getPlcU4() {
    var address = getWordAddress();

    if( address == MAX_WORDS - 1 )address--;

    if( address % 2 != 0 )address++;

    return new PlcU4( getWordDeviceCode(), address, getU4());
  }
  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcU4(int count) {
    return getWords( count, () -> getPlcU4() );
  }
  /**
   * @return
   */
  public static PlcI4 getPlcI4() {
    var address = getWordAddress();

    if( address == MAX_WORDS - 1 )address--;

    if( address % 2 != 0 )address++;

    return new PlcI4(getWordDeviceCode(), address, getI4());
  }
  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcI4(int count) {
    return getWords( count, () -> getPlcI4() );
  }
  /**
   * @return
   */
  public static PlcBit getPlcBit() {
    return getPlcBit( null );
  }
  /**
   *
   * @param device
   * @return
   */
  public static PlcBit getPlcBit( BitDeviceCode device ) {
    return new PlcBit( null == device ? getBitDeviceCode() : device,  getBitAddress(), getBit());
  }
  /**
   *
   * @param proto
   * @return
   */
  public static PlcBit flipPlcBit( PlcBit proto ){
    return ( PlcBit )Copier.withValue( proto, !proto.value());
  }
  /**
   *
   * @return
   */
  public static PlcString getPlcString(){
    return getPlcString( 10 );
  }
  /**
   * @return
   */
  public static PlcString getPlcString( int size ) {
    var address = random.nextInt( 0, MAX_WORDS );
    //var address = MAX_WORDS - 5;

    var extra = ( size % 2 == 0 ) ? 0 : 1;
    var points = size / 2 + extra;
    var right = address + points;

    address = ( right > MAX_WORDS ) ? address - ( right - MAX_WORDS ) : address;

    return new PlcString( getWordDeviceCode(),  address, size, getString( size ));
  }
  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getString( int size, int count) {
    return getWords( count, () -> getPlcString( size ) );
  }
  /**
   *
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcBit(int count) {
    return getPlcBit( count, null );
  }
  /**
   *
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcBit(int count, BitDeviceCode device ) {
    var list = new ArrayList<IPlcObject>();
    var set = new HashSet<PlcCoordinate>();

    for(int i = 0; i < count; ++i) {
      PlcBit next;

      do{
        next = getPlcBit( device );
      }
      while( set.contains( UtilityHelper.getCoordinate( next ) ) );

      list.add( next );
      set.add(UtilityHelper.getCoordinate( next ));
    }

    sort( list );

    return list;
  }
  /**
   *
   * @param list
   */
  public static void sort( List<IPlcObject> list ){
    Collections.sort( list, ( a, b ) -> ( a.device() == b.device() ) ?
      a.address() - b.address() : a.device().value() - b.device().value()  );
  }
  /**
   *
   * @return
   */
  public static int getWordAddress(){
    return random.nextInt(0, MAX_WORDS );
  }
  /**
   *
   * @return
   */
  public static int getBitAddress(){
    return random.nextInt(0, MAX_WORDS );
  }
  /**
   *
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcNumerics( int count ){
    DataType[] types = {
      I2, U2, I4, U4
    };

    var list = new ArrayList<IPlcObject>();
    var checker = new PlcCoordinate.IntersectionChecker();

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
      while( null != checker.add( ( IPlcWord ) next ) );

      list.add( next );
    }

    sort( list );

    return list;
  }
  /**
   *
   * @param sizeTo
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcString( int sizeFrom, int sizeTo, int count ){
    var list = new ArrayList<IPlcObject>();
    var checker = new PlcCoordinate.IntersectionChecker();

    for(int i = 0; i < count; ++i) {
      IPlcObject next = null;

      do{
        var size = random.nextInt( sizeFrom, sizeTo + 1 );
        next = getPlcString( size );
      }
      while( null != checker.add( ( IPlcWord ) next ) );

      list.add( next );
    }

    sort( list );

    return list;
  }
  /**
   *
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcWords( int count ){
    DataType[] types = {
      I2, U2, I4, U4, String, Struct
    };

    var list = new ArrayList<IPlcObject>();
    var checker = new PlcCoordinate.IntersectionChecker();

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
          case String -> getPlcString( random.nextInt( 1, 10 ) );
          case Struct -> getPlcStruct( random.nextInt( 2, 7 ) );
          default -> null;
        };
      }
      while( null != checker.add( ( IPlcWord ) next ) );

      list.add( next );
    }

    sort( list );

    return list;
  }
  /**
   *
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcBitNumerics( int count ){
    var nums = getPlcNumerics( count );
    var bits = getPlcBit( count );

    var res = new ArrayList<>( nums );
    res.addAll( bits );

    Collections.shuffle( res );

    return res;
  }
  /**
   * @param count
   * @return
   */
  private static <T extends IPlcObject> List<T> getWords(int count, Callable<T> func ) {
    var list = new ArrayList<T>();
    var checker = new PlcCoordinate.IntersectionChecker();

    for(int i = 0; i < count; ++i) {
      T next = null;

      do{
        try{
          next = func.call();
        }
        catch( Exception e ){

        }

      }
      while( null != checker.add( ( IPlcWord ) next ) );

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
  /**
   *
   * @param size
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcStruct( int size, int count) {
    return getWords( count, () -> getPlcStruct( size ) );
  }
  /**
   *
   * @return
   */
  public static PlcStruct getPlcStruct(){
    return getPlcStruct( 10 );
  }
  /**
   *
   * @param size
   * @return
   */
  public static PlcStruct getPlcStruct( int size ) {
    //var address = MAX_WORDS - 5;
    var address = getWordAddress();
    var b = PlcStruct.builder( getWordDeviceCode(), address );

    DataType[] types = {
      I2, U2, I4, U4, String, Bit
    };

    for( int i = 0; i < size; ){
      var index = random.nextInt( 0, types.length );
      var type = types[ index ];

      switch( type ){
        case I2 -> b.i2( getI2() );
        case I4 -> b.i4( getI4() );
        case U2 -> b.u2( getU2() );
        case U4 -> b.u4( getU4() );
        case String -> {
          var stringSize = random.nextInt( 0, 10 );
          b.string( stringSize, getString( stringSize ) );
        }
        case Bit -> b.offset( random.nextInt( 0, 5 ) );
      }

      if( type != Bit )i++;
    }

    var stub = b.build();

    var right = address + ByteConverter.getPointsCount( stub );

    address = ( right > MAX_WORDS ) ? address - ( right - MAX_WORDS ) : address;

    var res = Copier.withAddress( stub, address );

    return ( PlcStruct ) res;
  }
  /**
   *
   * @param list
   * @param count
   * @return
   */
  public static List<IPlcObject> select( List<IPlcObject> list, int count ){
    var res = new ArrayList<IPlcObject>( count );

    var set = new HashSet<Integer>();

    while( count > 0 ){
      var index = 0;

      do{
        index = random.nextInt( 0, list.size() );
      }
      while( set.contains( index ) );

      set.add( index );
      res.add( list.get( index ) );
      count--;
    }

    sort( res );

    return res;
  }
  /**
   *
   * @param list
   * @param count
   * @return
   */
  public static List<IPlcObject> update( List<IPlcObject> list, int count ) {
    return update( select( list, count ));
  }
  /**
   *
   * @param list
   * @return
   */
  public static List<IPlcObject> update( List<IPlcObject> list ){
    return list
      .stream()
      .map( x -> update( x ) )
      .toList();
  }
  /**
   *
   * @param o
   * @return
   */
  public static IPlcObject update( IPlcObject o ){
    var old = Valuer.getValue( o );
    Object v;

    return switch( o.type() ){
      case Bit -> flipPlcBit( ( PlcBit )o );
      case I2 -> {
        while( Valuer.equals( v = getI2(), o ) );
        yield Copier.withValue( o, v );
      }
      case U2 -> {
        while( Valuer.equals( v = getU2(), o ) );
        yield Copier.withValue( o, v );
      }
      case I4 -> {
        while( Valuer.equals( v = getI4(), o ) );
        yield Copier.withValue( o, v );
      }
      case U4 -> {
        while( Valuer.equals( v = getU4(), o ) );
        yield Copier.withValue( o, v );
      }
      case String -> {
        var so =  ( PlcString )o;
        while( Valuer.equals( v = getString( so.size() ), o ) && so.size() > 0 );
        yield Copier.withValue( o, v );
      }
      case Struct ->{
        var st = ( PlcStruct )o;

        var list = st
          .items()
          .stream()
          .map( x -> ( IPlcWord )update( x ) )
          .toList();

        yield Copier.withValue( st, list );
      }
      default -> null;
    };
  }

  public static List<IPlcObject> merge( List<IPlcObject> workingSet, List<IPlcObject> changes ){
    var res = new ArrayList<IPlcObject>();

    var dict = new HashMap<PlcCoordinate, IPlcObject>();

    for( var c : changes ){
      dict.put( UtilityHelper.getCoordinate( c ), c );
    }

    for( var next : workingSet ){
      var coord = UtilityHelper.getCoordinate( next );
      res.add( dict.containsKey( coord ) ? dict.get( coord ) : next );
    }

    return res;
  }
  //endregion
}

