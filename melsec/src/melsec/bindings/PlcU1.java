package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.Printer;
import melsec.utils.UtilityHelper;

public record PlcU1( WordDeviceCode device, int address, Integer value, String id )
  implements IPlcNumber<Integer> {

  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 0xFF;

  public PlcU1 {
    id = UtilityHelper.notNullString( id );

    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
  }

  @Override
  public DataType type(){
    return DataType.U1;
  }

  public PlcU1() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU1( WordDeviceCode device, int address ) {
    this( device, address, 0 );
  }

  public PlcU1( WordDeviceCode device, int address, String id ) {
    this( device, address, 0, id );
  }

  public PlcU1( WordDeviceCode device, int address, Integer value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return Printer.toString( this );
  }
}
