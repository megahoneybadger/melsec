package dispatcher.multi;

import melsec.Driver;
import melsec.bindings.*;
import melsec.io.IORequest;
import melsec.io.IORequestItem;
import melsec.io.IOType;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.Copier;
import utils.Console;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static melsec.types.DataType.*;

public class MultiWriteCommand extends BaseMultiCommand {

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
  public MultiWriteCommand(Driver c ){
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
          items.add( tryAddItem( address, type, value, id ));
          address = null;
          type = null;
          value = null;
          id = null;
        }
      }

      if( items.size() == 0 )
        throw new IllegalArgumentException( "No valid write items found" );

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
  private IORequestItem tryAddItem( String addr, String type, String value, String id ) {
    var device = validateDeviceCode( addr );
    var address = validateAddress( device, addr );
    var obj = validateType( device, address, type, id );

    if( device instanceof BitDeviceCode ){
      obj = new PlcBit((BitDeviceCode) device, address, value );
    }

    obj = Copier.with( obj, toTypedValue( obj, type, value ) );

    return new IORequestItem( IOType.Write, obj );
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
