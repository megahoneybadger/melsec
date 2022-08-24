package melsec.types;

import java.text.MessageFormat;

public interface IDeviceCode {

  int getValue();

  int getSectionSize();

  boolean isDecimalAddress();

  default int getAddressRadix(){
    return isDecimalAddress() ? 10 : 16;
  }

  default String toStringAddress( int a ){
    return isDecimalAddress() ?
      Integer.toString( a ):
      MessageFormat.format( "0x{0}", Integer.toString( a, 16 ) );
  }
}
