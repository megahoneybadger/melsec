package melsec.bindings.files;

import melsec.utils.XmlHelper;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class BindingReader {

  //region Class members
  /**
   *
   */
  private XMLStreamReader reader;
  /**
   *
   */
  private String path;
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
   */
  BindingReader( XMLStreamReader r, String p ){
    reader = r;
    path = p;
  }
  //endregion

  //region Class public methods
  public boolean shouldSkip(){
    return XmlHelper.shouldSkip( reader );
  }
  /**
   * **/
  public boolean shouldBreak( String n ){
    return XmlHelper.shouldBreak( reader, n );
  }
  /**
   *
   */
  public void readByEnd( String node ) throws XMLStreamException {
    XmlHelper.tillEnd( reader, node );
  }
  /**
   * **/
  public boolean hasNext() throws XMLStreamException {
    return reader.hasNext();
  }
  /**
   * **/
  public int next() throws XMLStreamException {
    return reader.next();
  }
  /**
   * **/
  public String getLocalName(){
    return reader.getLocalName();
  }
  /**
   * **/
  public String readAttrAsString( String name ){
    return XmlHelper.readAttrAsString( reader, name );
  }
  /**
   * **/
  public int readAttrAsInt( String name, int radix, int dv ){
    return XmlHelper.readAttrAsInt( reader, name, radix, dv );
  }
  /**
   *
   * @param name
   * @return
   */
  public short readAttrAsShort( String name ){
    return XmlHelper.readAttrAsShort( reader, name );
  }
  /**
   * **/
  public int readAttrAsInt( String name ){
    return XmlHelper.readAttrAsInt( reader, name );
  }
  public long readAttrAsLong( String name ){
    return XmlHelper.readAttrAsLong( reader, name );
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
    return XmlHelper.readAttrAsEnum( reader, name, type, dv );
  }
  /**
   * **/
  public boolean readAttrAsBool( String name ){
    return XmlHelper.readAttrAsBool( reader, name );
  }


  //endregion
}
