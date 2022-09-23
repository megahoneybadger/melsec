package melsec.types;

public record PlcCoordinate( IDeviceCode device, int address ) {

  public PlcCoordinate shiftleft(){
    return new PlcCoordinate( device, address - 1 );
  }

  public PlcCoordinate shiftRight(){
    return new PlcCoordinate( device, address + 1 );
  }
}
