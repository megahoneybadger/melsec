package melsec.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBit;
import melsec.types.CommandCode;
import melsec.commands.ICommand;
import melsec.types.exceptions.BadCompletionCodeException;
import melsec.types.exceptions.TooManyPointsException;
import melsec.types.io.IORequestItem;
import melsec.types.io.IORequestUnit;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MultiBlockBatchWriteCommand extends MultiBlockBatchBaseCommand {

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode getCode() {
    return CommandCode.MultiBlockBatchWrite;
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param u
   */
  public MultiBlockBatchWriteCommand( IORequestUnit u ){
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

      var condBlocksViolation = blocks >= MAX_BLOCKS;

      var condPointsViolation = 4 * blocks + points + itemPoints > MAX_POINTS;

      var shouldCreateCommand = condBlocksViolation || condPointsViolation;

      if( shouldCreateCommand && items.size() > 0 ){
        res.add( new MultiBlockBatchWriteCommand( unit.with( items ) ) );

        items.clear();
        blocks = 0;
        points = 0;
      }

      items.add( item );
      blocks++;
      points += itemPoints;
    }

    if( items.size() > 0 ){
      res.add( new MultiBlockBatchWriteCommand( unit.with( items ) ) );
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
    if( 4 * ( words.size() + bits.size() ) + getTotalPointsCount() > MAX_POINTS )
      throw new TooManyPointsException();

    int wordSize = 0;

    for( var x: words ) {
      wordSize += ( 3 + 1 + 2 + getPointsCount( x ) * 2 );
    }

    int bitSize = bits.size() * 8 /* 3 + 1 + 2 + 2*/;

    int totalSize = 2 + 6 + wordSize + bitSize;
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
  protected void encode( DataOutput w, Iterable<IPlcObject> list ) throws IOException {
    for( var item: list ) {
      encodeItemHeader( w, item );
      encodeItemBody( w, item );
    }
  }
  /**
   *
   * @param w
   * @param o
   * @return
   */
  protected void encodeItemBody( DataOutput w, IPlcObject o ) throws IOException {
    var buffer = switch( o.type() ){
      case Bit -> {
        // this is incorrect: I will not use bit writes with this command
        // just for tests
        short value = ( short )( ((PlcBit)o).value() ? 1 : 0 );
        yield ByteConverter.toBytes( value, 2 );
      }

      case I2, I4, U2, U4, String, Struct -> ByteConverter.toBytes( ( IPlcWord ) o );

      default -> null;
    };

    if( null != buffer ){
      w.write( buffer );
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

    results = Stream
      .concat( words.stream(), bits.stream())
      .toList();
  }
  //endregion
}
