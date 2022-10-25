package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

import static melsec.utils.UtilityHelper.EMPTY_STRING;

public record PlcBinary( IDeviceCode device, int address, int count,
                         byte[] value, String id ) implements IPlcWord  {

//  public PlcBinary {
//    value = UtilityHelper.coalesce( value, new byte[ count ] );
//    id = ( null == id ) ? EMPTY_STRING : id;
//
//    if( value.length != count ){
//      var arr = new byte[ count /*+ count % 2*/ ];
//
//      System.arraycopy( value, 0,
//        arr, 0, Math.min( arr.length, value.length ) );
//
//      value = arr;
//    }
//  }

  public PlcBinary( IDeviceCode device, int address, int count ) {
    this( device, address, count, null, EMPTY_STRING );
  }

  public PlcBinary( IDeviceCode device, int address, int count, byte[] value ) {
    this( device, address, count, value, EMPTY_STRING );
  }

  @Override
  public DataType type() {
    return DataType.Binary;
  }

  @Override
  public String toString(){
    return Stringer.toString( this );
  }
}
