package melsec.scanner;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBinary;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.PlcCoordinate;
import melsec.utils.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Cache {

  //region Class members
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private HashMap<IDeviceCode, ArrayList<IPlcObject>> protoStorage;
  /**
   *
   */
  private HashMap<PlcCoordinate, IPlcObject> latestStorage;
  //endregion

  //region Class initialization
  /**
   *
   * @param arr
   */
  public Cache( IPlcObject...arr ){
    this( List.of( arr ));
  }
  /**
   *
   * @param list
   */
  public Cache( List<IPlcObject> list ){
    protoStorage = new HashMap<>();
    syncObject = new Object();
    latestStorage = new HashMap<>();

    for( var o : list ){
      var deviceList = protoStorage.getOrDefault( o.device(), new ArrayList<>() );
      deviceList.add( o );
      protoStorage.put( o.device(), deviceList );
    }

    for( var entry : protoStorage.entrySet() ){
      Collections.sort( entry.getValue(),
        Comparator.comparingInt( IPlcObject::address ) );
    }
  }
  //endregion

  //region Class 'Update' methods
  /**
   *
   * @param bins
   */
  public List<IPlcObject> update( List<PlcBinary> bins ){
    var res = new ArrayList<IPlcObject>();

    synchronized( syncObject ){
      for( var bin : bins ) {
        var isBit = bin.device() instanceof BitDeviceCode;

        var next = isBit ?  updateBitCache( bin ) : updateWordCache( bin );

        if( null != next ){
          res.addAll( next );
        }
      }
    }

    return res;
  }
  /**
   *
   * @param bin
   */
  private List<IPlcObject> updateBitCache( PlcBinary bin ){
    var bindings = protoStorage.get( bin.device() );

    if( null == bindings )
      return null;

    var buffer = bin.value();

    var l = findFirstBinding( bin.address(), bindings );
    var r = findFirstBinding( bin.address() + buffer.length * 8, bindings );
    var res = new ArrayList<IPlcObject>( r - l );

    for( int i = l; i < r; ++i ){
      var b = bindings.get( i );

      var objAddress = b.address();

      objAddress -= bin.address();
      var byteAddress = objAddress / 8;
      var bitAddress = objAddress % 8;

      byte bt = buffer[ byteAddress ];
      byte mask = ( byte )( 1 << bitAddress );
      boolean value = ( ( mask & bt ) == mask );

      var coord = UtilityHelper.getCoordinate( b );

      var existingObject = latestStorage.get( coord );

      if( null == existingObject || !Valuer.equals( value, existingObject ) ){
        var newObject = Copier.withValue( b, value );
        latestStorage.put( coord, newObject );
        res.add( newObject );
      }
    }

    return res;
  }
  /**
   *
   * @param bin
   */
  private List<IPlcObject> updateWordCache( PlcBinary bin ){
    var bindings = protoStorage.get( bin.device() );

    if( null == bindings )
      return null;

    var res = new ArrayList<IPlcObject>();

    var buffer = ByteBuffer
      .wrap( bin.value() )
      .order( ByteOrder.LITTLE_ENDIAN );

    var l = findFirstBinding( bin.address(), bindings );

    for( int i = l; i < bindings.size(); ++i ){
      var b = bindings.get( i );
      var n = (IPlcWord)b;
      var objAddress = b.address();

      var notInRange =
        ( objAddress < bin.address()) ||
        ( objAddress + ByteConverter.getPointsCount( n ) - 1 >= ( bin.address() + bin.value().length / 2 ));

      if( notInRange )
        break;

      var shift = ( n.address() - bin.address() ) * 2;
      var value = ByteConverter.fromBytes( buffer, shift, n );

      var coord = UtilityHelper.getCoordinate( b );
      var existingObject = latestStorage.get( coord );

      if( null == existingObject || !Valuer.equals( value, existingObject ) ){
        var newObject = Copier.withValue( b, value );
        latestStorage.put( coord, newObject );
        res.add( newObject );
      }
    }

    return res;
  }
  /**
   *
   * @param target
   * @param list
   * @return
   */
  private int findFirstBinding( int target, List<IPlcObject> list ){
    var l = 0;
    var r = list.size() - 1;

    while( l <= r ){
      var m = l + ( r - l ) / 2;
      var mv = list.get( m ).address();

      if( mv == target )
        return m;

      if( mv < target ){
        l = m + 1;
      } else {
        r = m - 1;
      }
    }

    return l;
  }
  //endregion
}
