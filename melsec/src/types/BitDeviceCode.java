package types;

import types.IDeviceCode;

import java.text.MessageFormat;

public enum BitDeviceCode implements IDeviceCode {

  X( 0x9C ),
  Y( 0x9D ),
  M( 0x90 ),
  L( 0x92 ),
  S( 0x98 ),
  B( 0xA0 ),
  F( 0x93 );

  private int value;

  public int getValue(){
    return value;
  }

  public int getSectionSize(){
    return 15360;
  }

  public boolean isDecimalAddress() {
    return switch ( this ){
      case M, L, S, F -> true;
      default -> false;
    };
  }

  BitDeviceCode( int v ){
    this.value = v;
  }

  public String toString(){
    return MessageFormat.format( "{0}{1}",
      super.toString(), isDecimalAddress() ? "d" : "h" );
  }
}
