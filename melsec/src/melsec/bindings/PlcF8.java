package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

public record PlcF8(IDeviceCode device, int address, Double value, String id )
  implements IPlcNumber<Double> {

  @Override
  public DataType type(){
    return DataType.F8;
  }

  @Override
  public int size(){
    return 8;
  }

  public PlcF8() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcF8( IDeviceCode device, int address ) {
    this( device, address, 0d );
  }

  public PlcF8( IDeviceCode device, int address, Double value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
