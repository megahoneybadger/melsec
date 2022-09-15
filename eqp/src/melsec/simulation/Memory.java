package melsec.simulation;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBit;
import melsec.bindings.PlcString;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.ByteConverter;

import java.util.BitSet;
import java.util.HashMap;

public class Memory {

  //region Class constants
  private final int MAX_BITS = 50000;
  private final int MAX_WORDS = 50000;
  //endregion

  //region Class members
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private HashMap<IDeviceCode, BitSet> bits;
  /**
   *
   */
  private HashMap<IDeviceCode, byte[]> words;
  //endregion

  //region Class initialization
  /**
   *
   */
  public Memory(){
    syncObject = new Object();
    bits = new HashMap<>();
    words = new HashMap<>();
  }
  //endregion

  //region Class 'Read' methods
  /**
   *
   * @param o
   * @return
   */
  public Object read( IPlcObject o ){
    return ( o.type() == DataType.Bit ) ?
      read( ( PlcBit ) o ) :  read( ( IPlcWord ) o );
  }
  /**
   *
   * @param b
   * @return
   */
  public boolean read( PlcBit b ){
    synchronized(syncObject){
      return bits
        .computeIfAbsent( b.device(), k -> new BitSet( MAX_BITS ) )
        .get( b.address() );
    }
  }
  /**
   *
   * @param w
   * @return
   */
  public Object read( IPlcWord w ){
    synchronized(syncObject){
      var memory = words.computeIfAbsent(
        w.device(), k -> new byte[ MAX_WORDS * 2 ] );

      var size = switch( w.type() ){
        case I2, U2 -> 2;
        case I4, U4 -> 4;
        case String -> ((PlcString) w ).size();
        default -> 0;
      };

      var address = 2 * w.address();

      var bShouldReportError =
        ( 0 > address ) ||
        ( w.address() >= MAX_WORDS ) ||
        ( w.address() + size / 2 > MAX_WORDS );

      if( bShouldReportError )
        return null;

      var target = new byte[ size ];
      System.arraycopy( memory, address, target, 0, size );

      return ByteConverter.fromBytes( target, w );
    }
  }

  public byte[] read(WordDeviceCode device, int start, int points ){
    synchronized(syncObject){
      var memory = words.computeIfAbsent(
        device, k -> new byte[ MAX_WORDS * 2 ] );

      int max = memory.length;
      int address = start * 2;
      points *= 2;

      if( 0 > start || max <= start )
        return null;

      int limit = ( address + points >= max ) ? max - address : points;

      var res = new byte [ limit ];

      System.arraycopy( memory, address, res, 0, limit );

      return res;
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param points
   * @return
   */
  public byte[] read(BitDeviceCode device, int start, int points ){
    synchronized(syncObject){
      var memory = bits.computeIfAbsent( device, k -> new BitSet( MAX_BITS ) );

      var count = points * 16;
      var temp = new boolean[ count ];

      for( int i = start, index = 0; index < count; ++i, ++index ){
        temp[ index ] = memory.get( i );
      }

      var res = new byte[ points * 2 ];

      for( int i = 0, byteIndex = 0, bitIndex = 0; i < count; ++i, ++bitIndex ){
        if( bitIndex == 8 ){
          bitIndex = 0;
          byteIndex++;
        }

        if( !temp[ i ] )
          continue;

        var flag = 1 << bitIndex;
        res[ byteIndex ] |= flag;
      }
      //write b100 true, b102 true

      return res;
    }
  }
  //endregion

  //region Class 'Write' methods
  /**
   *
   * @param o
   */
  public void write( IPlcObject o ){
    if( o.type() == DataType.Bit ){
      write( ( PlcBit ) o );
    } else{
      write( ( IPlcWord ) o );
    }
  }
  /**
   *
   * @param b
   */
  public void write( PlcBit b ){
    synchronized(syncObject){
      bits
        .computeIfAbsent( b.device(), k -> new BitSet( MAX_BITS ) )
        .set( b.address(), b.value() );
    }
  }
  /**
   *
   * @param w
   */
  public void write( IPlcWord w ){
    synchronized(syncObject){
      var memory = words.computeIfAbsent(
        w.device(), k -> new byte[ MAX_WORDS * 2 ] );

      var address = 2 * w.address();
      var source = ByteConverter.toBytes( w );

      var bShouldReportError =
        ( 0 > address ) ||
        ( w.address() >= MAX_WORDS ) ||
        ( w.address() + source.length / 2 > MAX_WORDS );

      if( bShouldReportError )
        return;

      System.arraycopy( source, 0, memory, address, source.length );
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param buffer
   */
  public void write( WordDeviceCode device, int start, byte[] buffer ){
    synchronized( syncObject ){
      var memory = words.computeIfAbsent(
        device, k -> new byte[ MAX_WORDS * 2 ] );

      int address = start * 2;

      var shouldReportError =
        ( 0 > address ) ||
        ( start >= MAX_WORDS ) ||
        ( start + buffer.length / 2 > MAX_WORDS );

      if( shouldReportError )
        return;

      System.arraycopy( buffer, 0, memory, address, buffer.length );
    }
  }
  //endregion
}
