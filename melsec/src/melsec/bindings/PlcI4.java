package melsec.bindings;

import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

public record PlcI4(WordDeviceCode device, int address, Integer value, String id )
  implements IPlcNumber<Integer> {

  public static final int MIN_VALUE = Integer.MIN_VALUE;
  public static final int MAX_VALUE = Integer.MAX_VALUE;

  public PlcI4 {
    id = UtilityHelper.notNullString( id );

    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
  }

  @Override
  public DataType type(){
    return DataType.I4;
  }

  public PlcI4() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcI4(WordDeviceCode device, int address ) {
    this( device, address, 0 );
  }

  public PlcI4(WordDeviceCode device, int address, String id ) {
    this( device, address, 0, id );
  }

  public PlcI4(WordDeviceCode device, int address, Integer value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return Stringer.toString( this );
  }

  @Override
  public IPlcNumber<Integer> with( Integer v ){
    return new PlcI4( device, address, v, id );
  }
}
