import melsec.bindings.*;

public class Main {
    public static void main(String[] args)
      throws InvalidDeviceCodeException, InvalidNumberException {

        var r = 542365l > ( long )0xFFFF_FFFFl;

        var st = PlcStruct
          .builder()
          .device( DeviceCode.W )
          .address( 10 )
          .u1( 10 )
          .u1( 20 )
          .u1( 30 )
          .string( 20, "fuck u" )
          .string( 10, "this is a vey long string" )
          .u2( 125 )
          .u4( 542365 )
          .offset( 5 )
          .build();

        System.out.println( st );

        System.out.println( "----" );
    }


}

