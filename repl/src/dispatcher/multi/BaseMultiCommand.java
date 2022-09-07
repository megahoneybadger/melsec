package dispatcher.multi;

import dispatcher.BaseCommand;
import melsec.Driver;
import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

import java.text.MessageFormat;

public abstract class BaseMultiCommand extends BaseCommand {

  //region Class constants

  /**
   *
   */
  protected final static String TYPE_U2 = "u2";
  /**
   *
   */
  protected final static String TYPE_U4 = "u4";
  /**
   *
   */
  protected final static String TYPE_I2 = "i2";
  /**
   *
   */
  protected final static String TYPE_I4 = "i4";
  /**
   *
   */
  protected final static String TYPE_STRING = "a";
  //endregion

  //region Class initialization
  /**
   *
   * @param c
   */
  public BaseMultiCommand(Driver c ){
    super( c );
  }
  //endregion

  //region Class 'Exec' methods
  /**
   *
   * @param address
   * @return
   */
  protected IDeviceCode validateDeviceCode(String address ) {
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
  /**
   *
   * @param device
   * @param address
   * @return
   */
  protected int validateAddress( IDeviceCode device, String address  ){
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
  /**
   *
   * @param device
   * @param address
   * @param type
   * @param id
   * @return
   */
  protected IPlcObject validateType(IDeviceCode device, int address, String type, String id ) {
    if( device instanceof BitDeviceCode )
      return new PlcBit((BitDeviceCode) device, address, type );

    if( null == type || type.isEmpty() || type.isBlank() )
      throw new IllegalArgumentException( "Empty binding operation" );

    var stringObject = validateStringType( device, address, type, id );

    if( null != stringObject )
      return stringObject;

    return switch( type ){
      case TYPE_U2 -> new PlcU2( ( WordDeviceCode ) device, address, id );
      case TYPE_U4 -> new PlcU4( ( WordDeviceCode ) device, address, id );

      case TYPE_I2 -> new PlcI2( ( WordDeviceCode ) device, address, id );
      case TYPE_I4 -> new PlcI4( ( WordDeviceCode ) device, address, id );

      default -> throw new IllegalArgumentException( "Unknown binding operation" );
    };
  }
  /**
   *
   * @param device
   * @param address
   * @param type
   * @param id
   * @return
   */
  protected IPlcObject validateStringType( IDeviceCode device, int address, String type, String id ){
    var marker = type.charAt( 0 );

    if( !String.valueOf( marker ).equalsIgnoreCase( TYPE_STRING ))
      return null;

    try{
      var size = Short.parseShort( type.substring( 1 ) );

      if( size <= 0 )
        throw new IllegalArgumentException( "Invalid string operation's size" );;

      return new PlcString( ( WordDeviceCode ) device, address, size, "", id );
    }
    catch( NumberFormatException e ){}

    return null;
  }
  //endregion
}
