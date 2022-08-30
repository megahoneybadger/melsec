package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

public record PlcU1(IDeviceCode device, int address, Integer value, String id )
  implements IPlcNumber<Integer> {

  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 0xFF;

  public PlcU1 {
    id = ( null == id ) ? EMPTY_STRING : id;

    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
  }

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

  public PlcU1( IDeviceCode device, int address, String id ) {
    this( device, address, 0, id );
  }

  public PlcU1( IDeviceCode device, int address, Integer value ) {
    this( device, address, value, EMPTY_STRING );
  }
}
