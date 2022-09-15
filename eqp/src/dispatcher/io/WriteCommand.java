package dispatcher.io;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.io.IORequestItem;
import melsec.simulation.Equipment;
import melsec.types.BitDeviceCode;
import melsec.utils.Copier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class WriteCommand extends BaseIOCommand {

  //region Class constants
  /**
   *
   */
  public final static String COMMAND = "write";
  //endregion

  //region Class initialization
  /**
   *
   * @param c
   */
  public WriteCommand(Equipment c ){
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
      String value = null;
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
        else if( value == null ){
          value = next;
        }
        else if( id == null ){
          id = next;
        }

        if( detectedComma || args.get( args.size() - 1 ) == next ){
          write( address, type, value, id );
          address = null;
          type = null;
          value = null;
          id = null;
        }
      }
    }
    catch( Exception e ){
      System.err.println( MessageFormat
        .format( "Failed to write eqp memory. {0}", e.getMessage() ) );
    }
  }
  /**
   *
   * @param addr
   * @param type
   * @param id
   * @return
   */
  private void write( String addr, String type, String value, String id ) {
    var device = validateDeviceCode( addr );
    var address = validateAddress( device, addr );
    var obj = validateType( device, address, type, id );

    if( device instanceof BitDeviceCode){
      obj = new PlcBit((BitDeviceCode) device, address, value );
    }

    obj = Copier.with( obj, toTypedValue( obj, type, value ) );

    eqp.getMemory().write( obj );
  }
  /**
   *
    * @param proto
   * @param value
   * @return
   */
  private Object toTypedValue( IPlcObject proto, String type, String value ){
    try {
      return switch( proto.type() ){
        case Bit -> Boolean.parseBoolean( type/*!!!*/ );
        case U2, I4 -> Integer.parseInt( value );
        case U4 -> Long.parseLong( value );
        case I2 -> Short.parseShort( value );
        case String -> value;

        default -> throw new IllegalArgumentException( "Unknown value" );
      };
    }
    catch( Exception e ){
      throw new IllegalArgumentException(
        MessageFormat.format( "Invalid value {0} for {1}", value, proto.type() ) );
    }

  }
  //endregion
}
