package melsec.bindings;

public class PlcU2 extends PlcNumber<Integer> {

  @Override
  public PlcDataType type(){
    return PlcDataType.U2;
  }

  public PlcU2( String name, int v )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( name, v );
  }

  public PlcU2( DeviceCode device, int address )
    throws InvalidDeviceCodeException, InvalidNumberException {
    this( device, address, ( short )0 );
  }

  public PlcU2( DeviceCode device, int address, int value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address, value );
  }

  protected void validateNumber( Integer value ) throws InvalidNumberException {
    if( value < 0 || value > 0xFFFF )
      throw new InvalidNumberException( value, "U2" );
  }
}
