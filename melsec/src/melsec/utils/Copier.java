package melsec.utils;

import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.WordDeviceCode;

public class Copier {

  public static IPlcObject with(IPlcObject target, Object value ){
    var device = target.device();
    var addr = target.address();
    var id = target.id();

    return switch( target.type() ){
      case Bit -> new PlcBit( (BitDeviceCode) device, addr, ( boolean )value, id );
      case U1 -> new PlcU1( (WordDeviceCode)device, addr, ( int )value, id );
      case U2 -> new PlcU2( ( WordDeviceCode )device, addr, ( int )value, id );
      case U4 -> new PlcU4( ( WordDeviceCode )device, addr, ( long )value, id );

      default -> null;
    };
  }
}
