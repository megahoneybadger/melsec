package melsec.bindings;

import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

public record PlcBit( BitDeviceCode device, int address, boolean value, String id ) implements IPlcObject {

  public PlcBit {
    id = UtilityHelper.notNullString( id );
  }

  public DataType type(){
    return DataType.Bit;
  }

  public PlcBit() {
    this( BitDeviceCode.B, 0 );
  }

  public PlcBit( BitDeviceCode device, int address ) {
    this( device, address, false );
  }

  public PlcBit( BitDeviceCode device, int address, boolean value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public PlcBit( BitDeviceCode device, int address, String id ) {
    this( device, address, false, id );
  }

  public String toString() {
    return Stringer.toString( this );
  }
}

