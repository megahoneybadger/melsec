package dispatcher;

import melsec.Driver;
import melsec.bindings.*;
import melsec.io.IORequest;
import melsec.io.IORequestItem;
import melsec.io.IOType;
import melsec.types.BitDeviceCode;
import melsec.types.DeviceKind;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import utils.Console;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadCommand extends BaseCommand  {
  public final static String COMMAND = "read";

  public ReadCommand(Driver c ){
    super( c );
  }

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

  private IORequestItem tryAddItem( String addr, String type, String id ) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    var device = validateDeviceCode( addr );
    var address = validateAddress( device, addr );
    var obj = validateType( device, address, type, id );

    return new IORequestItem( IOType.Read, obj );
  }

  private IDeviceCode validateDeviceCode( String address ) {
    if( null == address || address.isEmpty() )
      throw new IllegalArgumentException( "Empty device code" );

    var ch = address.charAt( 0 );

    for( var next : BitDeviceCode.class.getEnumConstants()){
      if( next.name().equalsIgnoreCase( String.valueOf( ch ) ) ){
        return next;
      }
    }

    for( var next : WordDeviceCode.class.getEnumConstants()){
      if( next.name().equalsIgnoreCase( String.valueOf( ch ) ) ){
        return next;
      }
    }

    throw new IllegalArgumentException(MessageFormat.format( "Invalid device code: {0}", ch ));
  }

  private int validateAddress( IDeviceCode device, String address  ){
    var rest = address.substring( 1 );

    try{
      return device.isDecimalAddress() ?
        Integer.parseInt( rest ) :
        Integer.parseInt( rest, 16 );
    }
    catch( Exception e ){
      throw new IllegalArgumentException( MessageFormat.format(
        "Invalid address [{0}] for device {1}", rest, device ) );
    }
  }

  private IPlcObject validateType( IDeviceCode device, int address, String type, String id ) {
    if( device.getKind() == DeviceKind.Bit )
      return new PlcBit((BitDeviceCode) device, address, type );

    return switch( type ){
      case "u1" -> new PlcU1( ( WordDeviceCode ) device, address, id );
      case "u2" -> new PlcU2( ( WordDeviceCode ) device, address, id );
      case "u4" -> new PlcU4( ( WordDeviceCode ) device, address, id );

      default -> throw new IllegalArgumentException( "Unknown binding type" );
    };
  }


}

// read w100 u2 GlassId