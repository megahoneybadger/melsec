package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.utils.Stringer;

import static melsec.utils.UtilityHelper.EMPTY_STRING;

public record PlcBinary( IDeviceCode device, int address, int size,
                         byte[] value, String id ) implements IPlcObject {

  public PlcBinary( IDeviceCode device, int address, int size ) {
    this( device, address, size, null, EMPTY_STRING );
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
