package melsec.bindings;

import java.text.MessageFormat;

public class PlcString extends PlcObject {
  private int size;
  private String value;

  public String value(){
    return this.value;
  }

  public int size(){
    return size;
  }

  public int length(){
    return this.value.length();
  }

  @Override
  public PlcDataType type(){
    return PlcDataType.String;
  }

  public PlcString( int size ) throws InvalidDeviceCodeException {
    super( DeviceCode.W, 0 );

    this.size = size;
    this.value = "";
  }

  public PlcString( DeviceCode code, int address, int size ) throws InvalidDeviceCodeException {
    this( code, address, size,  "" );
  }

  public PlcString( DeviceCode code, int address, int size, String value ) throws InvalidDeviceCodeException {
    super( code, address );

    this.size = size;
    this.value = ( null == value ) ? "" : value;
  }

  protected void validateDevice( DeviceCode device ) throws InvalidDeviceCodeException {
    device.ensureWord();
  }



  @Override
  public boolean equals(Object obj ) {
    if( null == obj )
      return false;

    if( this == obj )
      return true;

    if( !( obj instanceof PlcString ) )
      return false;

    var s = ( PlcString )obj;

    if( size != s.size )
      return false;

    if( !value.equals( s.value ))
      return false;

    return super.equals( obj );
  }

  @Override
  public String toString(){
    return MessageFormat.format( "{5} [{0}@{1}] [{2}] {3}:{4}",
      super.device, device.toStringAddress( address ),
      value(), size(), length(), type() );
  }


}
