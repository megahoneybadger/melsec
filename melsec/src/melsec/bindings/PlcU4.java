package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.Printer;

public record PlcU4(WordDeviceCode device, int address, Long value, String id )
  implements IPlcNumber<Long> {

//  public PlcU4 {
//    id = ( null == id ) ? EMPTY_STRING : id;
//
//    value = ( value > MAX_VALUE ) ? MAX_VALUE : value;
//    value = ( value < MIN_VALUE ) ? MIN_VALUE : value;
//  }

  @Override
  public DataType type(){
    return DataType.U4;
  }

  public PlcU4() {
    this( WordDeviceCode.W, 0 );
  }

  public PlcU4( WordDeviceCode device, int address ) {
    this( device, address, 0l );
  }

  public PlcU4( WordDeviceCode device, int address, String id ) {
    this( device, address, 0l, id );
  }

  public PlcU4( WordDeviceCode device, int address, Long value ) {
    this( device, address, value, EMPTY_STRING );
  }

  public String toString() {
    return Printer.toString( this );
  }
}
