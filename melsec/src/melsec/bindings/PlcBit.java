package melsec.bindings;

import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.IDeviceCode;

import java.text.MessageFormat;

public record PlcBit(IDeviceCode device, int address, boolean value, String id ) implements IPlcObject {

  public PlcBit {
    id = ( null == id ) ? EMPTY_STRING : id;
  }

  public DataType type(){
    return DataType.Bit;
  }

  public PlcBit() {
    this( BitDeviceCode.B, 0 );
  }

  public PlcBit( IDeviceCode device, int address ) {
    this( device, address, false );
  }

  public PlcBit( IDeviceCode device, int address, boolean value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return MessageFormat.format("bit [{0}@{1}] [{2}] ",
      device, device.toStringAddress(address), value());
  }
}

