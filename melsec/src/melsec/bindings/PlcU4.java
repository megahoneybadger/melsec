package melsec.bindings;

import types.DataType;
import types.IDeviceCode;
import types.WordDeviceCode;

public record PlcU4(IDeviceCode device, int address, Long value, String id )
  implements IPlcNumber<Long> {

  @Override
  public DataType type(){
    return DataType.U4;
  }

  @Override
  public int size(){
    return 4;
  }

  public PlcU4() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU4( IDeviceCode device, int address ) {
    this( device, address, 0l );
  }

  public PlcU4( IDeviceCode device, int address, Long value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
