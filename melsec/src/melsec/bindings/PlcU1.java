package melsec.bindings;

public class PlcU1 extends PlcNumber<Integer> {

  @Override
  public PlcDataType type(){
    return PlcDataType.U1;
  }

  public PlcU1(String name, int v )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( name, v );
  }

  public PlcU1(DeviceCode device, int address )
    throws InvalidDeviceCodeException, InvalidNumberException {
    this( device, address, 0 );
  }

  public PlcU1(DeviceCode device, int address, int value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address, value );
  }

  protected void validateNumber( Integer value ) throws InvalidNumberException {
    if( value < 0 || value > 0xFF )
      throw new InvalidNumberException( value, "U1" );
  }
}
