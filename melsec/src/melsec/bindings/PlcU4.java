package melsec.bindings;

public class PlcU4 extends PlcNumber<Integer> {

  @Override
  public PlcDataType type(){
    return PlcDataType.U4;
  }

  public PlcU4( String name, int v )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( name, v );
  }

  public PlcU4( DeviceCode device, int address )
    throws InvalidDeviceCodeException, InvalidNumberException {
    this( device, address, 0 );
  }

  public PlcU4( DeviceCode device, int address, int value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address, value );
  }

  protected void validateNumber( Long value ) throws InvalidNumberException {
    if( value < 0 || value >  0xFFFF_FFFFl )
      throw new InvalidNumberException( value, "U4" );
  }
}
