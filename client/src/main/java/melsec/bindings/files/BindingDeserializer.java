package melsec.bindings.files;

import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.exceptions.XmlContentException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class BindingDeserializer extends BindingXmlBase {

  //region Class members
  private ArrayList<IPlcObject> results;
  /**
   * I keep the name as short as possible for reading convenience
   */
  private BindingReader r;
  //endregion

  //region Class initialization
  /**
   *
   * @param paths
   * @return
   */
  public static List<IPlcObject> read(String ...paths ) throws BindingDeserializationException {
    var res = new ArrayList<IPlcObject>();

    for( var p : paths ){
      res.addAll( BindingDeserializer.read( p ));
    }

    return res;
  }
  /**
   *
   * @param path
   * @return
   */
  private static List<IPlcObject> read(String path ) throws BindingDeserializationException {
    return new BindingDeserializer( path ).results;
  }
  /**
   *
   * @param path
   */
  private BindingDeserializer( String path ) throws BindingDeserializationException {
    BindingValidator.checkFile( path );

    try (var fs = new FileInputStream( path )) {
      var reader = XMLInputFactory
        .newInstance()
        .createXMLStreamReader(fs);

      results = new ArrayList<>();

      r = new BindingReader( reader, path );

      deserialize();
    }
    catch( Exception e) {
      throw new BindingDeserializationException( e.getMessage()  );
    }
  }
  //endregion

  //region Class 'Read' methods
  /**
   *
   */
  private void deserialize() throws XMLStreamException, XmlContentException {
    while( r.hasNext() ){
      r.next();

      if( r.shouldSkip())
        continue;

      switch( r.getLocalName() ){
        case ELEMENT_ROOT:
          readRoot();
          break;
      }
    }
  }
  /**
   *
   * @throws XMLStreamException
   */
  private void readRoot() throws XMLStreamException, XmlContentException {
    while( r.hasNext() ){
      r.next();

      if( r.shouldBreak( ELEMENT_ROOT ) )
        break;

      if( r.shouldSkip())
        continue;

      var node = r.getLocalName();

      var element = switch( node ){
        case ELEMENT_BIT -> readBit();
        case ELEMENT_I2 -> readWord( DataType.I2 );
        case ELEMENT_U2 -> readWord( DataType.U2 );
        case ELEMENT_I4 -> readWord( DataType.I4 );
        case ELEMENT_U4 -> readWord( DataType.U4 );
        case ELEMENT_STRING -> readWord( DataType.String );
        case ELEMENT_STRUCT -> readStruct();
        default -> null;
      };

      if( null != element ){
        results.add( element );
      }

      r.readByClosingTag( node );
    }
  }
  /**
   *
   * @return
   */
  private PlcBit readBit() throws XmlContentException {
    var id = r.readAttrAsString( ATTR_ID );

    var device = r.readAttrAsEnum( ATTR_DEVICE,
      BitDeviceCode.class, BitDeviceCode.B );

    var address = readAddress( device );

    var value = r.readAttrAsBool( ATTR_VALUE );

    return new PlcBit( device, address, value, id );
  }
  /**
   *
   * @param type
   * @return
   * @throws XmlContentException
   */
  private IPlcWord readWord( DataType type ) throws XmlContentException {
    var id = r.readAttrAsString(ATTR_ID);

    var device = r.readAttrAsEnum(ATTR_DEVICE,
      WordDeviceCode.class, WordDeviceCode.W);

    var address = readAddress( device );

    var value = readValue( type );
    var size = 0;

    if( type == DataType.String ){
      size = r.readAttrAsInt( ATTR_SIZE );

      if( size <= 0 )
        throw new XmlContentException( r, "Invalid string size [{0}]", size );
    }

    return switch( type ){
      case I2 -> new PlcI2( device, address, ( Short )value, id );
      case U2 -> new PlcU2( device, address, ( int )value, id );
      case I4 -> new PlcI4( device, address, ( int )value, id );
      case U4 -> new PlcU4( device, address, ( long )value, id );
      case String -> new PlcString( device, address, size, ( String )value, id );
      default -> null;
    };
  }
  /**
   *
   * @return
   */
  private PlcStruct readStruct() throws XmlContentException, XMLStreamException {
    var id = r.readAttrAsString(ATTR_ID);

    var device = r.readAttrAsEnum(ATTR_DEVICE,
      WordDeviceCode.class, WordDeviceCode.W);

    var address = readAddress( device );

    var b = PlcStruct.builder( device, address, id );

    while( r.hasNext() ){
      r.next();

      if( r.shouldBreak( ELEMENT_STRUCT ) )
        break;

      if( r.shouldSkip())
        continue;

      var node = r.getLocalName();

      switch( node ){
        case ELEMENT_I2 ->{
          var item = readWordItem( DataType.I2 );
          b.i2( ( short )item.value, item.id );
        }
        case ELEMENT_U2 ->{
          var item = readWordItem( DataType.U2 );
          b.u2( ( int )item.value, item.id );
        }
        case ELEMENT_I4 ->{
          var item = readWordItem( DataType.I4 );
          b.i4( ( int )item.value, item.id );
        }
        case ELEMENT_U4 ->{
          var item = readWordItem( DataType.U4 );
          b.u4( ( long )item.value, item.id );
        }
        case ELEMENT_STRING ->{
          var item = readWordItem( DataType.String );
          b.string( item.size, ( String )item.value, item.id );
        }
        case ELEMENT_OFFSET ->{
          b.offset( readWordOffset() );
        }

        default -> r.readByClosingTag( node );
      };
    }

    return b.build();
  }
  /**
   *
   * @param type
   * @return
   * @throws XmlContentException
   */
  private WordItem readWordItem( DataType type ) throws XmlContentException, XMLStreamException {
    var id = r.readAttrAsString(ATTR_ID);

    var value = readValue( type );
    var size = 0;

    if( type == DataType.String ){
      size = r.readAttrAsInt( ATTR_SIZE );

      if( size <= 0 )
        throw new XmlContentException( r, "Invalid string size [{0}]", size );
    }

    r.readByClosingTag( type.toString() );

    return new WordItem( size, value, id );
  }
  /**
   *
   * @return
   * @throws XmlContentException
   */
  private int readWordOffset() throws XmlContentException, XMLStreamException {
    var size = r.readAttrAsInt( ATTR_SIZE );

    if( size <= 0 )
      throw new XmlContentException( r, "Invalid offset size [{0}]", size );

    r.readByClosingTag( ELEMENT_OFFSET );

    return size;
  }
  /**
   *
   * @param type
   * @return
   * @throws XmlContentException
   */
  private Object readValue( DataType type ) throws XmlContentException {
    return switch( type ){
      case I2 -> r.readAttrAsShort( ATTR_VALUE );
      case U2 -> {
        var value = r.readAttrAsInt( ATTR_VALUE );

        if( value < 0 || value > 0xFFFF )
          throw new XmlContentException( r, "Invalid value [{0}] for U2", Integer.valueOf( value ).toString() );

        yield value;
      }
      case I4 ->  r.readAttrAsInt( ATTR_VALUE );
      case U4 -> {
        var value = r.readAttrAsLong( ATTR_VALUE );

        if( value < 0 || value > 0xFFFF_FFFFl )
          throw new XmlContentException( r, "Invalid value [{0}] for U4", Long.valueOf( value ).toString()  );

          yield value;
      }
      case String -> r.readAttrAsString( ATTR_VALUE );
      default -> null;
    };
  }
  /**
   *
   * @param device
   * @return
   * @throws XmlContentException
   */
  private int readAddress( IDeviceCode device ) throws XmlContentException {
    var radix = device.isDecimalAddress() ? 10 : 16;

    int address = r.readAttrAsInt( ATTR_ADDRESS, radix, BAD_INDEX );

    if( address == BAD_INDEX || address < 0 || address > ( 1 << 24 ) )
      throw new XmlContentException( r, "Invalid address" );

    return address;
  }
  //endregion

  //region Class internal structs
  /**
   *
   * @param size
   * @param value
   * @param id
   */
  record WordItem( int size, Object value, String id ){}
  //endregion
}
