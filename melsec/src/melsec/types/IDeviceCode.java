package melsec.types;

public interface IDeviceCode {

  int value();

  int getSectionSize();

  boolean isDecimalAddress();

  default int getAddressRadix(){
    return isDecimalAddress() ? 10 : 16;
  }

  default String toStringAddress( int a ){
    return isDecimalAddress() ?
      Integer.toString( a ):
      String.format("%04X", a );

  }
}
