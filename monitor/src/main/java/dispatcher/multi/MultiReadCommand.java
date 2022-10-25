package dispatcher.multi;

import melsec.net.EquipmentClient;
import melsec.types.io.IORequest;
import melsec.types.io.IORequestItem;
import melsec.types.io.IOType;
import utils.Console;

import java.util.ArrayList;
import java.util.List;

public class MultiReadCommand extends BaseMultiCommand {

  //region Class constants
  /**
   *
   */
  public final static String COMMAND = "read";
  //endregion

  //region Class initialization
  /**
   *
   * @param c
   */
  public MultiReadCommand(EquipmentClient c ){
    super( c );
  }
  //endregion

  //region Class 'Exec' methods
  /**
   *
   * @param args
   */
  @Override
  public void exec( List<String> args ){

    try{
      String address = null;
      String type = null;
      String id = null;
      var items = new ArrayList<IORequestItem>();

      for( var next: args ){
        next = next.trim();

        var detectedComma = ( next.contains( "," ) );

        if( detectedComma ){
          next = next.replaceAll( ",", "" );
        }

        if( address == null ){
          address = next;
        }
        else if( null == type ) {
          type = next;
        }
        else if( id == null ){
          id = next;
        }

        if( detectedComma || args.get( args.size() - 1 ) == next ){
          items.add( tryAddItem( address, type, id ));
          address = null;
          type = null;
          id = null;
        }
      }

      if( items.size() == 0 )
        throw new IllegalArgumentException( "No valid read items found" );

      var req = IORequest
        .builder()
        .add( items )
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
        .build();

      communicator.exec( req );
    }
    catch( Exception e ){
      Console.error( e );
    }

  }
  /**
   *
   * @param addr
   * @param type
   * @param id
   * @return
   */
  private IORequestItem tryAddItem( String addr, String type, String id ) {
    var device = validateDeviceCode( addr );
    var address = validateAddress( device, addr );
    var obj = validateType( device, address, type, id );

    return new IORequestItem( IOType.Read, obj );
  }
  //endregion
}
