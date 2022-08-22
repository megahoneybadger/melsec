package melsec.bindings;

import java.text.MessageFormat;

public class PlcBit extends  PlcObject {
  private boolean value;

  public boolean value(){
    return this.value;
  }

  @Override
  public PlcDataType type(){
    return PlcDataType.Bit;
  }

  public PlcBit() throws InvalidDeviceCodeException  {
    super( DeviceCode.B, 0 );
  }

  public PlcBit( String name, boolean value ) throws InvalidDeviceCodeException  {
    this();

    this.value = value;
    super.name = name;
  }

  public PlcBit( DeviceCode device, int address ) throws InvalidDeviceCodeException {
    this( device, address, false );
  }

  public PlcBit( DeviceCode device, int address, boolean value ) throws InvalidDeviceCodeException {
    super( device, address );

    this.value = value;
  }

  protected void validateDevice(DeviceCode device ) throws InvalidDeviceCodeException {
    device.ensureBit();
  }

  protected void ValidateAddress( int address ){
    // todo later
  }

  public String toString(){
    return MessageFormat.format( "bit [{0}@{1}] [{2}] ",
      super.device, device.toStringAddress( address ), value() );
  }

  @Override
  public boolean equals(Object obj ) {
    if( null == obj )
      return false;

    if( this == obj )
      return true;

    if( !( obj instanceof PlcBit ) )
      return false;

    PlcBit b = ( PlcBit )obj;

    if( value != b.value )
      return false;

    return super.equals( obj );
  }
}
