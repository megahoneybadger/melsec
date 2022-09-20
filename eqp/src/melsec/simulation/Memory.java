package melsec.simulation;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBit;
import melsec.exceptions.InvalidRangeException;
import melsec.simulation.handlers.RequestBlock;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.ByteConverter;

import java.util.BitSet;
import java.util.HashMap;

public class Memory {

  //region Class constants
  /**
   *
   */
  private final int MAX_BITS = 50000;
  /**
   *
   */
  private final int MAX_WORDS = 5;
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
  public Object read( IPlcObject o ) throws InvalidRangeException {
    return ( o.type() == DataType.Bit ) ?
      read( ( PlcBit ) o ) :  read( ( IPlcWord ) o );
  }
  /**
   *
   * @param b
   * @return
   */
  public boolean read( PlcBit b ) throws InvalidRangeException {
    synchronized(syncObject){
      return getMemory( b.device(), b.address(), 1 ).get( b.address() );
    }
  }
  /**
   *
   * @param w
   * @return
   */
  public Object read( IPlcWord w ) throws InvalidRangeException {
    var buffer = getBytes( new RequestBlock( w.device(),
      w.address(), ByteConverter.getPointsCount( w ) ) );

    return ByteConverter.fromBytes( buffer, w );
  }
  /**
   *
   * @param b
   * @return
   * @throws InvalidRangeException
   */
  public byte[] getBytes( RequestBlock b ) throws InvalidRangeException {
    return ( b.device() instanceof BitDeviceCode ) ?
      getBytes( ( BitDeviceCode ) b.device(), b.address(), b.points() ) :
      getBytes( ( WordDeviceCode ) b.device(), b.address(), b.points() );
  }
  /**
   *
   * @param device
   * @param start
   * @param points
   * @return
   */
  public byte[] getBytes( WordDeviceCode device, int start, int points ) throws InvalidRangeException {
    synchronized( syncObject ){
      var size = 2 * points;
      var address = 2 * start;
      var memory = getMemory( device, address, points );

      var target = new byte[ size ];
      System.arraycopy( memory, address, target, 0, size );

      return target;
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param points
   * @return
   */
  public byte[] getBytes( BitDeviceCode device, int start, int points ) throws InvalidRangeException {
    synchronized(syncObject){
      var memory = getMemory( device, start, points );

      var count = points * 16;
      var temp = new boolean[ count ];

      for( int i = start, j = 0; j < count; ++i, ++j ){
        temp[ j ] = memory.get( i );
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

      return res;
    }
  }
  //endregion

  //region Class 'Write' methods
  /**
   *
   * @param o
   */
  public void write( IPlcObject o ) throws InvalidRangeException {
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
  public void write( PlcBit b ) throws InvalidRangeException {
    synchronized(syncObject){
      getMemory( b.device(), b.address(), 1 )
        .set( b.address(), b.value() );
    }
  }
  /**
   *
   * @param w
   */
  public void write( IPlcWord w ) throws InvalidRangeException {
    setBytes( ( WordDeviceCode  )w.device(), w.address(), ByteConverter.toBytes( w ) );
  }
  /**
   *
   * @param b
   * @throws InvalidRangeException
   */
  public void setBytes( RequestBlock b ) throws InvalidRangeException {
    if( b.device() instanceof BitDeviceCode ){
      setBytes( ( BitDeviceCode ) b.device(), b.address(), b.buffer() );
    } else{
      setBytes( ( WordDeviceCode ) b.device(), b.address(), b.buffer() );
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param buffer
   */
  public void setBytes( WordDeviceCode device, int start, byte[] buffer ) throws InvalidRangeException {
    synchronized( syncObject ){
      var address = start * 2;
      var points = buffer.length / 2;

      var memory = getMemory( device, address, points );

      System.arraycopy( buffer, 0, memory, address, buffer.length );
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param buffer
   */
  public void setBytes( BitDeviceCode device, int start, byte[] buffer ) throws InvalidRangeException {
    synchronized( syncObject ){
      var points = buffer.length / 2;
      var memory = getMemory( device, start, points );

      for( int i = 0, index = start; i < buffer.length; ++i ){
        var b = buffer[ i ];

        for( int j = 0; j < 8; ++j, b >>= 1, ++index ){
          memory.set( index, ( b & 1 ) > 0 );
        }
      }
    }
  }
  //endregion

  //region Class utility methods
  /**
   *
   */
  public void reset(){
    synchronized( syncObject ){
      bits.clear();
      words.clear();
    }
  }
  /**
   *
   * @param device
   * @param start
   * @param points
   */
  private BitSet getMemory( BitDeviceCode device, int start, int points ) throws InvalidRangeException {
    var memory = bits
      .computeIfAbsent( device, k -> new BitSet( MAX_BITS ) );

    var l = start;
    var r = start + points * 16 - 1;

    var invalidRange = ( l < 0 ) || ( /*r*/ l >= MAX_BITS );

    if( invalidRange )
      throw new InvalidRangeException( device, l, r, MAX_BITS - 1 );

    return memory;
  }
  /**
   *
   * @param device
   * @param start
   * @param points
   * @return
   * @throws InvalidRangeException
   */
  private byte[] getMemory( WordDeviceCode device, int start, int points ) throws InvalidRangeException {
    var memory = words.computeIfAbsent( device, k -> new byte[ MAX_WORDS * 2 ] );

    var l = start;
    var r = start + 2 * points - 1;

    var invalidRange = ( l < 0 ) || ( r >= 2 * MAX_WORDS );

    if( invalidRange )
      throw new InvalidRangeException( device, l / 2, r / 2, MAX_WORDS - 1 );

    return memory;
  }
  //endregion
}
