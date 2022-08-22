package melsec.bindings;

public class PlcOffset extends PlcObject {

  private int size;

  public int value(){
    return this.size;
  }

  @Override
  public PlcDataType type(){
    return PlcDataType.Offset;
  }

  public PlcOffset( DeviceCode device, int address, int size ) throws InvalidDeviceCodeException {
    super( device, address );

    this.size = size;
  }

  protected void validateDevice( DeviceCode device ) throws InvalidDeviceCodeException {
    device.ensureWord();
  }
}
