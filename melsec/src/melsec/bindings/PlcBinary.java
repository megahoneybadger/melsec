package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.utils.Stringer;

import static melsec.utils.UtilityHelper.EMPTY_STRING;

public record PlcBinary( IDeviceCode device, int address, int count,
                         byte[] value, String id ) implements IPlcWord  {

  public PlcBinary( IDeviceCode device, int address, int count ) {
    this( device, address, count, null, EMPTY_STRING );
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
