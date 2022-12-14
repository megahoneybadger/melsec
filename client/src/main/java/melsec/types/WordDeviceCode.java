package melsec.types;

import java.text.MessageFormat;

public enum WordDeviceCode implements IDeviceCode {
  D( 0xA8 ),
  W( 0xB4 ),
  R( 0xB0);

  private int value;

  public int value(){
    return value;
  }

  public boolean isDecimalAddress() {
    return switch ( this ){
      case D, R -> true;
      default -> false;
    };
  }

  WordDeviceCode( int v ){
    this.value = v;
  }

  public String toString(){
    return MessageFormat.format( "{0}{1}",
      super.toString(), isDecimalAddress() ? "d" : "h" );
  }
}


