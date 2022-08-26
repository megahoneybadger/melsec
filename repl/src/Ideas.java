public class Ideas {

  public void todo(){

//    var s = LogManager.ROOT_LOGGER_NAME;
//    var log = LogManager.getLogger( "sucker" );
//
//    log.error( "fuck u asshole john" );

//    var ep = new Endpoint( InetAddress.getByName( "127.0.0.1" ) , 5000 );
//
//    var s = ep.address().getHostAddress();
//
//    var conn = new Connection( ep );
//    conn.start();
//
//    conn.subscribe( new ConnectionObserver() );
//
//    System.out.println( "press any key" );
//    var scanInput = new Scanner(System.in);
//    scanInput.nextLine();

//    var u1 = new PlcU1( BitDeviceCode.B, 10, -10, null );
//
//    var comm = new Communicator();
//
//    var r = IORequest
//      .builder()
//      .read( PlcStruct
//        .builder( WordDeviceCode.D, 10, "GLASS" )
//        .u1( 10, "Age" )
//        .u1( 20, "Height" )
//        .build())
//      .write( new PlcBit( BitDeviceCode.B, 10, true, "GlassReplyBit" ) )
//      .build();
//
//    comm.exec( r, response -> {
//      var successItems = response
//        .items()
//        .stream()
//        .filter( x -> x.result().success() )
//        .toList();
//
//      for( var item: successItems ){
//        System.out.println( item.result().value() );
//      }
//
//      var failureItems = response
//        .items()
//        .stream()
//        .filter( x -> x.result().failure() )
//        .toList();
//
//      for( var item: failureItems ){
//        var message = MessageFormat.format( "{0}: {1}",
//          item.request().toString(), item.result().error() );
//        System.out.println( message );
//      }
//    });

    // IOPackage
    //  .builder
    //  .read(  )
    //  .write(  )
    //  .build()


//        var comm = new Communicator();
//        comm
//          .reader()
//          .add( new PlcU1( BitDeviceCode.L, 10 ),
//            new OperationHandler() {
//                public void completed(IPlcObject result, OperationLog log) {
//
//                }
//
//                public void failed(ErrorCode error, OperationLog log) {
//
//                }
//            });
//
//        var st = PlcStruct
//          .builder( WordDeviceCode.D, 10, "GLASS" )
//          .u1( 10, "Age" )
//          .u1( 20, "Height" )
//          .u1( 30 )
//          .string( 20, "hello u", "Description" )
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
