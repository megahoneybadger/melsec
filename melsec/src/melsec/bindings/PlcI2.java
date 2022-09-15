package melsec.bindings;

import melsec.types.DataType;
import melsec.types.WordDeviceCode;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

public record PlcI2( WordDeviceCode device, int address, Short value, String id )
  implements IPlcNumber<Short> {

  public static final short MIN_VALUE = Short.MIN_VALUE;
  public static final short MAX_VALUE = Short.MAX_VALUE;

  public PlcI2 {
    id = UtilityHelper.notNullString( id );

    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
  }

  @Override
  public DataType type(){
    return DataType.I2;
  }

  public PlcI2() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcI2(WordDeviceCode device, int address ) {
    this( device, address, ( short )0 );
  }

  public PlcI2(WordDeviceCode device, int address, String id ) {
    this( device, address, ( short )0, id );
  }

  public PlcI2( WordDeviceCode device, int address, Short value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return Stringer.toString( this );
  }

  @Override
  public IPlcNumber<Short> with( Short v ){
    return new PlcI2( device, address, v, id );
  }
}
//65535