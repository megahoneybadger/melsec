package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

public record PlcU2(IDeviceCode device, int address, Integer value, String id )
  implements IPlcNumber<Integer> {

  @Override
  public DataType type(){
    return DataType.U2;
  }

  @Override
  public int size(){
    return 2;
  }

  public PlcU2() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU2( IDeviceCode device, int address ) {
    this( device, address, 0 );
  }

  public PlcU2( IDeviceCode device, int address, Integer value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
