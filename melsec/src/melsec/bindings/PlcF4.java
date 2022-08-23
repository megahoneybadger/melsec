package melsec.bindings;

import types.DataType;
import types.IDeviceCode;
import types.WordDeviceCode;

public record PlcF4(IDeviceCode device, int address, Float value, String id )
  implements IPlcNumber<Float> {

  @Override
  public DataType type(){
    return DataType.F4;
  }

  @Override
  public int size(){
    return 4;
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
