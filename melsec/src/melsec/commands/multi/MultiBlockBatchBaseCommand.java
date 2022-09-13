package melsec.commands.multi;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.io.*;
import melsec.commands.CommandCode;
import melsec.types.DataType;
import melsec.utils.Copier;
import melsec.utils.EndianDataInputStream;
import melsec.utils.UtilityHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultiBlockBatchBaseCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  protected static final int MAX_POINTS = 960;
  /**
   *
   */
  protected static final int MAX_BLOCKS = 120;
  //endregion

  //region Class members
  /**
   *
   */
  protected HashMap<String, IORequestItem> map;
  /**
   *
   */
  protected List<IPlcObject> results;
  /**
   *
   */
  protected List<IPlcObject> bits;
  /**
   *
   */
  protected List<IPlcObject> words;
  //endregion

  //region Class properties
  /**
   *
   * @param o
   * @return
   */
  protected static int getPointsCount( IPlcObject o ){
    return switch( o.type() ){
      case Bit, U2, I2 -> 1;
      case U4, I4 -> 2;
      case String -> {
        var s = ( PlcString ) o;
        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;
        var points = s.size() / 2;
        yield ( points + extra );
      }
      case Struct -> {
        var st = ( PlcStruct )o;
        var items = st.items();

        if( 0 == items.size() )
          yield 0;

        var last = items.get( items.size() - 1 );
        var words = last.address() - st.address() + getPointsCount( last );

        yield words;
      }
      default -> 0;
    };
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param u
   */
  public MultiBlockBatchBaseCommand( IORequestUnit u ){
    map = new HashMap<>();
    results = new ArrayList<>();
    unit = u;

    bits = UtilityHelper.toList( u.items() )
      .stream()
      .filter( x -> x.object().type() == DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    words = UtilityHelper.toList( u.items() )
      .stream()
      .filter( x -> x.object().type() != DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    UtilityHelper.toList( u.items() )
      .stream()
      .forEach( x -> map.put( UtilityHelper.getKey( x.object()), x ) );
  }
  /**
   *
   * @return
   */
  @Override
  public String toString(){
    String w = words.size() > 0 ? MessageFormat.format( "{0}w", words.size() ) : "";
    String b = bits.size() > 0 ? MessageFormat.format( "{0}b", bits.size() ) : "";
    String sep = ( !w.isEmpty() && !b.isEmpty() ) ? "|" : "";

    var c = ( code() == CommandCode.MultiBlockBatchWrite ) ? "w" : "r";

    return MessageFormat.format( "mbb{4}#{3} [{0}{1}{2}]", w, sep, b, id, c );
  }
  //endregion

  //region Class 'Coding' methods
  /**
   *
   * @param w
   * @param o
   * @return
   */
  protected void encode( DataOutput w, IPlcObject o ) throws IOException {
    var val = switch( o.type() ){
      case I2, I4, U2, U4 -> ((IPlcNumber)o ).value();
      case String -> (( PlcString )o).value();
      case Bit -> ((PlcBit)o).value();
      default -> null;
    };

    Function<Integer, ByteBuffer> getBuffer = (Integer size ) -> ByteBuffer
      .allocate( size )
      .order( ByteOrder.LITTLE_ENDIAN );

    var buffer = switch( o.type() ){

      case Bit -> {
        // this is incorrect
        short value = ( short )( ( boolean )val ? 1 : 0 );
        yield toBytes( value, 2 );
      }

      case I2 -> getBuffer
        .apply( 2 )
        .putShort( ( short )val )
        .array();

      case U2 -> getBuffer
        .apply( 2 )
        .putShort( ( short )( ( ( int )val & 0xFFFF ) )  )
        .array();

      case I4 -> getBuffer
        .apply( 4 )
        .putInt( ( int )val )
        .array();

      case U4 -> getBuffer
        .apply( 4 )
        .putInt( ( int )( ( ( long )val & 0xFFFF_FFFFl ) ) )
        .array();

      case String -> {
        var so = ( PlcString )o;
        int length = so.size() * 2;
        var text = ( String )val;

        if( text.length() < length ){
          text = java.lang.String.format( "%-" + length + "." + length + "s", text );
        } else if( text.length() > length ){
          text = text.substring( 0, length );
        }

        yield text.getBytes();
      }

      case Struct -> {
        var proto = (PlcStruct)o;
        var items = proto.items();
        var address = proto.address();

        for( var next : items ){
          var offset = next.address() - address;

          if( offset > 0 ){
            w.write( new byte[ 2 * offset ] );
          }

          encode( w, next );
          address += getPointsCount( next ) + offset;
        }

        yield null;
      }

      default -> null;
    };

    if( null != buffer ){
      w.write( buffer );
    }
  }
  /**
   *
   * @param r
   * @param o
   * @return
   * @throws IOException
   */
  protected IPlcObject decode( DataInput r, IPlcObject o ) throws IOException {
    var value = switch( o.type() ){
      case U2 -> r.readUnsignedShort();
      case I2 -> r.readShort();

      case U4 -> ((EndianDataInputStream)r).readUnsignedInt();
      case I4 -> r.readInt();

      case String -> {
        var s = ( PlcString )o;

        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;

        var buffer = new byte[ s.size() + extra ];
        r.readFully( buffer );

        var res = new String( buffer );
        res = res.substring( 0, Math.min( res.length(), s.size() ));

        yield  res;
      }

      case Struct -> {
        var proto = ( PlcStruct ) o;
        var list = new ArrayList<IPlcWord>();
        var items = proto.items();
        var address = proto.address();

        for( var next : items ){
          var offset = next.address() - address;

          if( offset > 0 ){
            r.skipBytes( 2 * offset );
          }

          var v = decode( r, next );
          address += getPointsCount( next ) + offset;

          list.add( ( IPlcWord ) v );
        }

        yield list;
      }

      default -> null;
    };

    return Copier.with( o, value );
  }
  //endregion

  //region Class 'Response' methods
  /**
   *
   * @return
   */
  public IOResponse toResponse(){
    var output = new HashMap<String, IOResponseItem>();

    for( var o : results ){
      var key = UtilityHelper.getKey( o );

      if( !map.containsKey( key ) )
        continue;

      var req = map.get( key );
      output.put( key, req.toResponse( o ) );
    }

    var res = new ArrayList<IOResponseItem>();

    for( var item: unit.items() ){
      var key = UtilityHelper.getKey( item.object());

      if( !output.containsKey( key ) )
        continue;

      res.add( output.get( key ) );
    }

    return new IOResponse( res );
  }
  //endregion
}
