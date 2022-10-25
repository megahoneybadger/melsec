package melsec.commands.multi;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.types.exceptions.BadCompletionCodeException;
import melsec.types.exceptions.TooManyPointsException;
import melsec.types.io.IORequestItem;
import melsec.types.io.IORequestUnit;
import melsec.types.CommandCode;
import melsec.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class MultiBlockBatchReadCommand extends MultiBlockBatchBaseCommand {

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode getCode() {
    return CommandCode.MultiBlockBatchRead;
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param u
   */
  public MultiBlockBatchReadCommand( IORequestUnit u ){
    super( u );
  }
  /**
   *
   * @param unit
   * @return
   */
  public static List<ICommand> split( IORequestUnit unit ){
    var res = new ArrayList<ICommand>();
    var items = new ArrayList<IORequestItem>();

    var blocks = 0;
    var points = 0;

    for( var item: unit.items() ){
      var itemPoints = getPointsCount( item.object() );

      var shouldCreateCommand =
        ( blocks >= MAX_BLOCKS ) || ( points + itemPoints > MAX_POINTS );

      if( shouldCreateCommand && items.size() > 0 ){
        res.add( new MultiBlockBatchReadCommand( unit.with( items ) ) );

        items.clear();
        blocks = 0;
        points = 0;
      }

      items.add( item );
      blocks++;
      points += itemPoints;
    }

    if( items.size() > 0 ){
      res.add( new MultiBlockBatchReadCommand( unit.with( items ) ) );
    }

    return res;
  }
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param w
   * @throws IOException
   */
  @Override
  protected void encode( DataOutput w ) throws IOException {
    if( getTotalPointsCount() > MAX_POINTS )
      throw new TooManyPointsException();

    int totalSize = 2 + 6 + ( bits.size() + words.size() ) * 6;
    encodePrologue( w, totalSize );

    encode( w, words );
    encode( w, bits );
  }
  /**
   *
   * @param w
   * @param list
   * @throws IOException
   */
  private void encode( DataOutput w, Iterable<IPlcObject> list ) throws IOException {
    for( var item: list ){
      encodeItemHeader( w, item );
    }
  }
  //endregion

  //region Class 'Decoding' methods
  /**
   *
   * @param r
   */
  @Override
  protected void decode( DataInput r ) throws IOException, BadCompletionCodeException {
    r.skipBytes( 7 );

    var dataSize = r.readUnsignedShort();
    var completionCode = r.readUnsignedShort();

    if( 0 != completionCode )
      throw new BadCompletionCodeException( completionCode );

    results.clear();
    results.addAll( decodeWords( r ));
    results.addAll( decodeBits( r ));
  }
  /**
   *
    * @param r
   * @return
   */
  protected List<PlcBit> decodeBits( DataInput r ) throws IOException {
    var list = new ArrayList<PlcBit>();

    for( var proto: bits ){
      // I am reading only one bit from the response
      var word = r.readUnsignedShort();
      var val = ( 1 == ( 1 & word ));
      list.add( ( PlcBit ) Copier.withValue( proto, val ) );
    }

    return list;
  }
  /**
   *
   * @param r
   * @return
   */
  protected List<IPlcWord> decodeWords( DataInput r ) throws IOException {
    var list = new ArrayList<IPlcWord>();

    for( var proto: words ){
      var o = decode ( r, proto );
      list.add( ( IPlcWord ) o );
    }

    return list;
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
        var s = (PlcString)o;

        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;

        var buffer = new byte[ s.size() + extra ];
        r.readFully( buffer );

        var res = new String( buffer );
        res = res
          .substring( 0, Math.min( res.length(), s.size() ))
          .trim();

        yield  res;
      }

      case Struct -> {
        var proto = (PlcStruct) o;
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

    return Copier.withValue( o, value );
  }
  //endregion
}
