package melsec.bindings;

import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

public record PlcU4(WordDeviceCode device, int address, Long value, String id )
  implements IPlcNumber<Long> {

  public static final long MIN_VALUE = 0l;
  public static final long MAX_VALUE = 0xFFFF_FFFFl;

  public PlcU4 {
    id = UtilityHelper.notNullString( id );

    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
  }

  @Override
  public DataType type(){
    return DataType.U4;
  }

  public PlcU4() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU4( WordDeviceCode device, int address ) {
    this( device, address, 0l );
  }

  public PlcU4( WordDeviceCode device, int address, String id ) {
    this( device, address, 0l, id );
  }

  public PlcU4( WordDeviceCode device, int address, Long value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return Stringer.toString( this );
  }

  @Override
  public IPlcNumber<Long> with( Long v ){
    return new PlcU4( device, address, v, id );
  }

  public IPlcNumber<Long> without(){
    return new PlcU4( device, address, 0l, id );
  }
}
