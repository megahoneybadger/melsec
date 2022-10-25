package melsec.types;

public interface IDeviceCode {

  int value();

  boolean isDecimalAddress();

  default String toStringAddress( int a ){
    return isDecimalAddress() ?
      Integer.toString( a ):
      String.format("%04X", a );
  }
}
