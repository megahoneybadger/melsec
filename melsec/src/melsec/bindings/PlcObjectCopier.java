package melsec.bindings;

public class PlcObjectCopier {

  public static IPlcObject with( IPlcObject target, Object value ){
    var device = target.device();
    var addr = target.address();
    var id = target.id();

    return switch( target.type() ){
      case Bit -> new PlcBit( device, addr, ( boolean )value, id );
      case U1 -> new PlcU1( device, addr, ( int )value, id );
      case U2 -> new PlcU2( device, addr, ( int )value, id );
      case U4 -> new PlcU4( device, addr, ( long )value, id );

      default -> null;
    };
  }
}
