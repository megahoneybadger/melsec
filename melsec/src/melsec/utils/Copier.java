package melsec.utils;

import melsec.bindings.*;

import java.util.List;

public class Copier {

  public static IPlcObject with( IPlcObject proto, Object value ){
    return switch( proto.type() ){
      case Bit -> (( PlcBit ) proto ).with( ( boolean )value );

      case U2 -> (( PlcU2 ) proto ).with( ( int )value );
      case U4 -> (( PlcU4 ) proto ).with( ( long )value );

      case I2 -> (( PlcI2 ) proto ).with( ( short )value );
      case I4 -> (( PlcI4 ) proto ).with( ( int )value );

      case String ->(( PlcString ) proto ).with( ( String )value );

      case Struct -> {
        var st = ( PlcStruct ) proto;
        yield PlcStruct.builder().with( st, ( List<IPlcWord> )value );
      }

      default -> null;
    };
  }

  public static PlcStruct with( PlcStruct p, List<IPlcWord> items ){
    return PlcStruct
      .builder()
      .with( p, items );
  }
}
