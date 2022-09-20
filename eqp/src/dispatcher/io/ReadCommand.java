package dispatcher.io;

import melsec.exceptions.InvalidRangeException;
import melsec.simulation.Memory;

import java.text.MessageFormat;
import java.util.List;

public class ReadCommand extends BaseIOCommand {

  //region Class constants
  /**
   *
   */
  public final static String COMMAND = "read";
  //endregion

  //region Class initialization
  /**
   *
   * @param m
   */
  public ReadCommand(Memory m ){
    super( m );
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
          read( address, type, id );
          address = null;
          type = null;
          id = null;
        }
      }
    }
    catch( Exception e ){
      System.err.println( MessageFormat
        .format( "Failed to read eqp memory. {0}", e.getMessage() ) );
    }

  }
  /**
   *
   * @param addr
   * @param type
   * @param id
   * @return
   */
  private void read( String addr, String type, String id ) throws InvalidRangeException {
    var device = validateDeviceCode( addr );
    var address = validateAddress( device, addr );
    var obj = validateType( device, address, type, id );

    var res = memory.read( obj );
    System.out.println( res );
  }
  //endregion
}
