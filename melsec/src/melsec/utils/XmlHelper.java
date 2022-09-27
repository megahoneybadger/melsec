package melsec.utils;

import melsec.types.IDeviceCode;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.exceptions.XmlContentException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

public class XmlHelper {

  //region Class constants
  /**
   *
   */
  static final String REGEX_IP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
  //endregion

  //region Class public methods
  /**
   *
   * @param path
   * @throws BindingDeserializationException
   */
  public static void validate( String path ) throws BindingDeserializationException {
    var f = new File( path );

    var fullPath = f.getAbsolutePath();

    if( !f.isFile())
      throw new BindingDeserializationException( "File [{0}] does not exist", fullPath );

    try {
      DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse( fullPath );
    }
    catch( Exception e ){
      throw new BindingDeserializationException(
        "File [{0}] contains invalid xml. {1}", fullPath, e.getMessage() );
    }
  }

  /**
   *
   * @param r
   * @param node
   * @return
   */
  public static boolean shouldBreak( XMLStreamReader r, String node ){
    return ( r.isEndElement() ) && node.equalsIgnoreCase( r.getLocalName() );
  }

  /**
   *
   * @param r
   * @throws XMLStreamException
   */
  public static void tillEnd( XMLStreamReader r, String node ) throws XMLStreamException {
    var count = 0;

    do{
      if( r.isStartElement() )count++;

      if( r.isEndElement() ){
        count = Math.max( 0, count - 1 );
      }

      if( !r.hasNext() || ( 0 == count && r.getLocalName() == node ) )
        break;

      r.next();
    }
    while( true );
  }

  /**
   *
   * @param r
   * @return
   */
  public static boolean shouldSkip( XMLStreamReader r ) {
    return ( r.isEndElement() || !r.isStartElement() );
  }
  /**
   * */
  public static String readAttrAsString( XMLStreamReader r, String name ){
    return r.getAttributeValue( "", name );
  }

  /**
   *
   * @param r
   * @param name
   * @return
   */
  public static int readAttrAsInt( XMLStreamReader r, String name ){
    String v = r.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return 0;

    try {
      return Integer.parseInt( v );
    }
    catch( Exception e ){
    }

    return 0;
  }

  /**
   *
   * @param r
   * @param name
   * @return
   */
  public static long readAttrAsLong( XMLStreamReader r, String name ){
    String v = r.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return 0l;

    try {
      return Long.parseLong( v );
    }
    catch( Exception e ){
    }

    return 0l;
  }

  /**
   *
   * @param r
   * @param name
   * @return
   */
  public static Short readAttrAsShort( XMLStreamReader r, String name ){
    String v = r.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return (short)0;

    try {
      return Short.parseShort( v );
    }
    catch( Exception e ){
    }

    return ( short )0;
  }

  /**
   *
   * @param r
   * @param name
   * @param radix
   * @param dv
   * @return
   */
  public static int readAttrAsInt( XMLStreamReader r, String name, int radix, int dv ){
    String v = r.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return dv;

    try {
      if( v.startsWith( "0x" ) && radix == 16 )
        return Integer.decode( v );

      return Integer.parseInt( v, radix );
    }
    catch( Exception e ){
    }

    return dv;
  }

  /**
   *
   * @param r
   * @param name
   * @return
   */
  public static boolean readAttrAsBool( XMLStreamReader r, String name ){
    String v = r.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return false;

    try {
      return Boolean.parseBoolean( v );
    }
    catch( Exception e ){
    }

    return false;
  }

  /**
   *
   * @param r
   * @param name
   * @param type
   * @param dv
   * @return
   * @param <T>
   */
  public static <T extends Enum<T>> T readAttrAsEnum( XMLStreamReader r, String name, Class<T> type, T dv ){
    return readStringAsEnum( r.getAttributeValue( "", name ), type, dv );
  }
  /*
   * **/
  public static <T extends Enum<T>> T readStringAsEnum( String v, Class<T> type, T dv ){
    T res = dv;

    try {
      for (T enumValue : type.getEnumConstants()) {
        if (enumValue.name().equalsIgnoreCase(v)) {
          return enumValue;
        }
      }

    }catch( Exception e ){

    }

    return res;
  }

  //endregion
}
