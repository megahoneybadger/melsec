package melsec.io.commands;

import melsec.bindings.*;
import melsec.io.IOCompleteEventHandler;
import melsec.types.DataType;
import melsec.utils.EndianDataInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MultiBlockBatchReadCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  public static final int MAX_POINTS = 960;
  /**
   *
   */
  public static final int MAX_BLOCKS = 120;
  //endregion

  //region Class members
  /**
   *
   */
  private List<IPlcObject> bits;
  /**
   *
   */
  private List<IPlcObject> words;
  /**
   *
   */
  private IOCompleteEventHandler completeHandler;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode code() {
    return CommandCode.MultiBlockBatchRead;
  }
  /**
   *
   * @param o
   * @return
   */
  private static int getPointsCount( IPlcObject o ){
    return switch( o.type() ){
      case Bit, U1, U2, I1, I2 -> 1;
      case U4, I4, F4 -> 2;
      case U8, I8, F8 -> 4;
      case String -> {
        var s = ( PlcString ) o;
        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;
        var points = s.size() / 2;
        yield ( points + extra );
      }
      default -> 0;
    };
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param targets
   * @param completeHandler
   */
  public MultiBlockBatchReadCommand( Iterable<IPlcObject> targets, IOCompleteEventHandler completeHandler ) {
    this.completeHandler = completeHandler;

    bits = StreamSupport
      .stream( targets.spliterator(), false )
      .filter( x -> x.type() == DataType.Bit )
      .collect( Collectors.toList() );

    words = StreamSupport
      .stream( targets.spliterator(), false )
      .filter( x -> x.type() != DataType.Bit )
      .collect( Collectors.toList() );
  }
  /**
   *
   * @param items
   * @param handler
   * @return
   */
  public static List<MultiBlockBatchReadCommand> split(
    Iterable<IPlcObject> items, IOCompleteEventHandler handler ){

    var res = new ArrayList<MultiBlockBatchReadCommand>();
    var group = new ArrayList<IPlcObject>();

    var blocks = 0;
    var points = 0;

    for( var item: items ){
      var itemPoints = getPointsCount( item );

      var shouldCreateCommand =
        ( blocks >= MAX_BLOCKS ) || ( points + itemPoints > MAX_POINTS );

      if( shouldCreateCommand ){
        res.add( new MultiBlockBatchReadCommand( new ArrayList<>( group ), handler ) );
        group.clear();
        blocks = 0;
        points = 0;
      }

      blocks++;
      points += itemPoints;
      group.add( item );
    }

    res.add( new MultiBlockBatchReadCommand( new ArrayList<>( group ), handler ) );

    return res;
  }
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param writer
   * @throws IOException
   */
  @Override
  protected void encode( DataOutput writer ) throws IOException {
    int iTotalSize = 2 + 6 + ( bits.size() + words.size() ) * 6;
    Coder.encodeHeader( writer, iTotalSize );

    // Command
    writer.writeByte( 0x06 );
    writer.writeByte( 0x04 );

    // Subcommand 00 means use children units (bit packed in word bits)
    writer.writeByte( 0x00 );
    writer.writeByte( 0x00 );

    // Number of word device blocks
    writer.writeByte( words.size() );

    // Number of bit device blocks
    writer.writeByte( bits.size() );

    encode( writer, words );

    encode( writer, bits );
  }
  /**
   *
   * @param writer
   * @param list
   * @throws IOException
   */
  private void encode( DataOutput writer, Iterable<IPlcObject> list ) throws IOException {
    for( var item: list ){
      // Word device number
      writer.write( Coder.toBytes( item.address(), 3 ) );

      // Device code
      writer.write( item.device().getValue() );

      // Number of device points
      var size = getPointsCount( item );
      writer.write( Coder.toBytes( size, 2 ) );
    }
  }
  //endregion

  //region Class 'Decoding' methods
  /**
   *
   * @param reader
   */
  @Override
  protected void decode( DataInput reader ) throws IOException {

    reader.skipBytes( 7 );

    var dataSize = Coder.toUShort( reader.readShort() );
    var completionCode = Coder.toUShort( reader.readShort() );

//    if( 0 != completionCode )
//      throw new RtException( RtException.Code.BadCompletionCode, completionCode );

    var list = new ArrayList<>();

    list.addAll( decodeWords( reader ));
    list.addAll( decodeBits( reader ));
  }
  /**
   *
    * @param reader
   * @return
   */
  private List<PlcBit> decodeBits( DataInput reader ) throws IOException {
    var list = new ArrayList<PlcBit>();

    for( var proto: bits ){
      var val = ( 1 == ( 1 & reader.readUnsignedShort() ));
      list.add( ( PlcBit ) PlcObjectCopier.with( proto, val ) );
    }

    return list;
  }
  /**
   *
   * @param reader
   * @return
   */
  private List<IPlcWord> decodeWords( DataInput reader ) throws IOException {
    var list = new ArrayList<IPlcWord>();

    for( var proto: words ){
      var o = ( proto.type() == DataType.Struct ) ?
        decodeStruct( reader, ( PlcStruct ) proto ) :
        decodeWord( reader, ( IPlcWord ) proto );

      list.add( o );
    }

    return list;
  }
  /**
   *
   * @param reader
   * @param proto
   * @return
   */
  private PlcStruct decodeStruct( DataInput reader, PlcStruct proto ){

    return null;
  }
  /**
   *
   * @param reader
   * @param proto
   * @return
   */
  private IPlcWord decodeWord( DataInput reader, IPlcWord proto ) throws IOException {
    var value = Coder.decodeValue( reader, proto.type() );

    return ( IPlcWord ) PlcObjectCopier.with( proto, value );
  }
  //endregion
}
