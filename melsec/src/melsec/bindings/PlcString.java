package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

import java.text.MessageFormat;

public record PlcString(IDeviceCode device, int address, int size,
                        String value, String id )  implements IPlcWord {

  public PlcString {
    value = ( null == value ) ? EMPTY_STRING : value;
    id = ( null == id ) ? EMPTY_STRING : id;

    value = value.substring(0, Math.min( value.length(), size ));
  }

  @Override
  public DataType type() {
    return DataType.String;
  }

  public int length(){
    return this.value.length();
  }

  public PlcString( int size ) {
    this( WordDeviceCode.W, 0, size, EMPTY_STRING, EMPTY_STRING );
  }

  public PlcString( IDeviceCode device, int address, int size ) {
    this( device, address, size, EMPTY_STRING, EMPTY_STRING );
  }

  public PlcString(IDeviceCode device, int address, int size, String value ) {
    this( device, address, size, value, EMPTY_STRING );
  }

  @Override
  public String toString(){
    return MessageFormat.format( "A{3} [{0}@{1}] [{2}]",
      device, device.toStringAddress( address ), value(), size() );
  }
}