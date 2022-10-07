package melsec.scanner;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBinary;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.PlcCoordinate;
import melsec.utils.Copier;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;
import melsec.utils.Valuer;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        ( a, b ) -> a.address() - b.address() );
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

        res.addAll( isBit ?  updateBitCache( bin ) : updateWordCache( bin ) );
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
    var res = new ArrayList<IPlcObject>();

    if( null == bindings )
      return res;

    var buffer = bin.value();

    for( var b : bindings ){
      var objAddress = b.address();

      var notInRange =
        (objAddress < bin.address()) ||
        (objAddress >= ( bin.address() + buffer.length * 8 ));

      if( notInRange )
        continue;

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
    var res = new ArrayList<IPlcObject>();

    return res;
  }
  //endregion
}
