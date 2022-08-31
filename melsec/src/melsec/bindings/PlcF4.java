package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

public record PlcF4(IDeviceCode device, int address, Float value, String id )
  implements IPlcNumber<Float> {

  @Override
  public DataType type(){
    return DataType.F4;
  }

  public PlcF4() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcF4( IDeviceCode device, int address ) {
    this( device, address, 0f );
  }

  public PlcF4( IDeviceCode device, int address, Float value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
