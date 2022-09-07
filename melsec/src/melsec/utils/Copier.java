package melsec.utils;

import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.WordDeviceCode;

import java.util.List;

public class Copier {

  public static IPlcObject with( IPlcObject proto, Object value ){
    var device = proto.device();
    var addr = proto.address();
    var id = proto.id();

    return switch( proto.type() ){
      case Bit -> new PlcBit( (BitDeviceCode) device, addr, ( boolean )value, id );

      case U2 -> new PlcU2( ( WordDeviceCode )device, addr, ( int )value, id );
      case U4 -> new PlcU4( ( WordDeviceCode )device, addr, ( long )value, id );

      case I2 -> new PlcI2( ( WordDeviceCode )device, addr, ( short )value, id );
      case I4 -> new PlcI4( ( WordDeviceCode )device, addr, ( int )value, id );

      case String -> new PlcString( ( WordDeviceCode )device,
        addr, (( PlcString )proto ).size(), ( String )value, id );


      default -> null;
    };


  }

  public static PlcStruct with( PlcStruct p, List<IPlcWord> items ){
    return PlcStruct
      .builder()
      .with( p, items );
  }
}
