package melsec.bindings;

import java.text.MessageFormat;

public abstract class PlcNumber<T extends  Number> extends PlcObject {

  protected T value;

  public T value(){
    return value;
  }

  protected PlcNumber( String name, T value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( DeviceCode.W, 0 );

    this.value = value;
    super.name = name;

    validateNumber( value );
  }

  public PlcNumber( DeviceCode device, int address, T value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address );

    this.value = value;

    validateNumber( value );
  }

  @Override
  protected void validateDevice(DeviceCode device ) throws InvalidDeviceCodeException {
    device.ensureWord();
  }

  protected void validateNumber( T value ) throws InvalidNumberException {

  }

  @Override
  public boolean equals(Object obj ) {
    if( null == obj )
      return false;

    if( this == obj )
      return true;

    if( !( obj instanceof PlcNumber ) )
      return false;

    PlcNumber b = ( PlcNumber )obj;

    if( !value.equals( b.value ) )
      return false;

    return super.equals( obj );
  }

  @Override
  public String toString(){
    return MessageFormat.format( "{3} [{0}@{1}] [{2}]",
      super.device, device.toStringAddress( address ), value(), type() );
  }
}
