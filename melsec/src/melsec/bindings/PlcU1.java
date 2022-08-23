package melsec.bindings;

import types.DataType;
import types.IDeviceCode;
import types.WordDeviceCode;

public record PlcU1(IDeviceCode device, int address, Integer value, String id )
  implements IPlcNumber<Integer> {

  @Override
  public DataType type(){
    return DataType.U1;
  }

  @Override
  public int size(){
    return 1;
  }

  public PlcU1() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU1( IDeviceCode device, int address ) {
    this( device, address, 0 );
  }

  public PlcU1( IDeviceCode device, int address, Integer value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
