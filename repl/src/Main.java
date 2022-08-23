import comm.Communicator;
import melsec.bindings.*;
import types.BitDeviceCode;
import types.ErrorCode;
import types.OperationHandler;
import types.OperationLog;

public class Main {
    public static void main(String[] args) {

        var comm = new Communicator();
        comm
          .reader()
          .add( new PlcU1( BitDeviceCode.L, 10 ),
            new OperationHandler() {
                public void completed(IPlcObject result, OperationLog log) {

                }

                public void failed(ErrorCode error, OperationLog log) {

                }
            });
//
//        var st = PlcStruct
//          .builder( WordDeviceCode.D, 10, "GLASS" )
//          .u1( 10, "Age" )
//          .u1( 20, "Height" )
//          .u1( 30 )
//          .string( 20, "fuck u", "Description" )
//          .string( 10, "this is a vey long string", null )
//          .u2( 125 )
//          .u4( 542365 )
//          .offset( 5 )
//          .u2( 156 )
//          .offset( 2 )
//          .u2( 200 )
//          .build();
//
//        var size = st.size();
//
//        System.out.println( st );
//
//        System.out.println( "----" );
    }


}

