package melsec.bindings;

public class PlcF4 extends PlcNumber<Float> {

  @Override
  public PlcDataType type(){
    return PlcDataType.F4;
  }

  public PlcF4( String name, float v )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( name, v );
  }

  public PlcF4( DeviceCode device, int address )
    throws InvalidDeviceCodeException, InvalidNumberException {
    this( device, address, 0f );
  }

  public PlcF4( DeviceCode device, int address, float value )
    throws InvalidDeviceCodeException, InvalidNumberException {
    super( device, address, value );
  }
}
