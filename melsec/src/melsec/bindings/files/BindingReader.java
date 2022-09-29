package melsec.bindings.files;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class BindingReader {

  //region Class members
  /**
   *
   */
  private final XMLStreamReader reader;
  /**
   *
   */
  private final String path;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public XMLStreamReader getNativeReader() {
    return reader;
  }
  /**
   *
   * @return
   */
  public String getPath() {
    return path;
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param r
   * @param p
   */
  public BindingReader( XMLStreamReader r, String p ){
    reader = r;
    path = p;
  }
  //endregion

  //region Class 'Navigation' methods
  /**
   *
   * @param node
   * @return
   */
  public boolean shouldBreak( String node ){
    return ( reader.isEndElement() ) && node.equalsIgnoreCase( reader.getLocalName() );
  }
  /**
   *
   * @return
   */
  public boolean shouldSkip() {
    return ( reader.isEndElement() || !reader.isStartElement() );
  }
  /**
   *
   * @param node
   * @throws XMLStreamException
   */
  public void readByClosingTag( String node ) throws XMLStreamException {
    var count = 0;

    do{
      if( reader.isStartElement() )count++;

      if( reader.isEndElement() ){
        count = Math.max( 0, count - 1 );
      }

      if( !reader.hasNext() || ( 0 == count && reader.getLocalName() == node ) )
        break;

      reader.next();
    }
    while( true );
  }
  /**
   *
   * @return
   */
  public String getLocalName(){
    return reader.getLocalName();
  }
  /**
   *
   * @return
   * @throws XMLStreamException
   */
  public boolean hasNext() throws XMLStreamException {
    return reader.hasNext();
  }
  /**
   *
   * @return
   * @throws XMLStreamException
   */
  public int next() throws XMLStreamException {
    return reader.next();
  }
  //endregion

  //region Class 'Read Attribute' methods
  /**
   *
   * @param name
   * @return
   */
  public String readAttrAsString( String name ){
    return reader.getAttributeValue( "", name );
  }
  /**
   *
    * @param name
   * @return
   */
  public int readAttrAsInt( String name ){
    String v = reader.getAttributeValue( "", name );

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
   * @param name
   * @return
   */
  public long readAttrAsLong( String name ){
    String v = reader.getAttributeValue( "", name );

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
   * @param name
   * @return
   */
  public Short readAttrAsShort( String name ){
    String v = reader.getAttributeValue( "", name );

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
   * @param name
   * @param radix
   * @param dv
   * @return
   */
  public int readAttrAsInt( String name, int radix, int dv ) {
    String v = reader.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return dv;

    try {
      if( v.startsWith( "0x" ) && radix == 16 )
        return Integer.decode( v );

      return Integer.parseInt( v, radix );
    }
    catch( Exception e ) {
    }

    return dv;
  }
  /**
   * @param name
   * @return
   */
  public boolean readAttrAsBool( String name ) {
    String v = reader.getAttributeValue( "", name );

    if( null == v || 0 == v.length() )
      return false;

    try {
      return Boolean.parseBoolean( v );
    }
    catch( Exception e ) {
    }

    return false;
  }
  /**
   *
   * @param name
   * @param type
   * @param dv
   * @return
   * @param <T>
   */
  public <T extends Enum<T>> T readAttrAsEnum( String name, Class<T> type, T dv ){
    return readStringAsEnum( reader.getAttributeValue( "", name ), type, dv );
  }
  /*
   * **/
  public <T extends Enum<T>> T readStringAsEnum( String v, Class<T> type, T dv ){
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
