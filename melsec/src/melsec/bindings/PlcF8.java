package melsec.bindings;

public class PlcF8 extends PlcNumber<Double> {

  @Override
  public PlcDataType type(){
    return PlcDataType.F8;
  }

  public PlcF8( String name, double v )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( name, v );
  }

  public PlcF8( DeviceCode device, int address )
    throws InvalidDeviceCodeException, InvalidNumberException {
    this( device, address, 0 );
  }

  public PlcF8( DeviceCode device, int address, double value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address, value );
  }
}
