package melsec.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBit;
import melsec.bindings.PlcStruct;
import melsec.utils.Coder;
import melsec.commands.ICommand;
import melsec.exceptions.BadCompletionCodeException;
import melsec.io.IORequestItem;
import melsec.io.IORequestUnit;
import melsec.commands.CommandCode;
import melsec.types.DataType;
import melsec.utils.Copier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MultiBlockBatchReadCommand extends MultiBlockBatchBaseCommand {

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode code() {
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
  public static List<ICommand> split(IORequestUnit unit ){
    var res = new ArrayList<ICommand>();
    var items = new ArrayList<IORequestItem>();

    var blocks = 0;
    var points = 0;

    for( var item: unit.items() ){
      var itemPoints = getPointsCount( item.object() );

      var shouldCreateCommand =
        ( blocks >= MAX_BLOCKS ) || ( points + itemPoints > MAX_POINTS );

      if( shouldCreateCommand ){
        res.add( new MultiBlockBatchReadCommand( unit.with( items ) ) );
        items.clear();
        blocks = 0;
        points = 0;
      }

      blocks++;
      points += itemPoints;
      items.add( item );
    }

    if( items.size() > 0 ){
      res.add( new MultiBlockBatchReadCommand( unit.with( items ) ) );
    }

    return res;
  }
  /**
   *
   * @return
   */
  @Override
  public ICommand copy(){
    var copy = new MultiBlockBatchReadCommand( unit );
    copy.id = id;
    return copy;
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
      writer.write( item.device().value() );

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
  protected void decode( DataInput reader ) throws IOException, BadCompletionCodeException {
    reader.skipBytes( 7 );

    var dataSize = Coder.toUShort( reader.readShort() );
    var completionCode = Coder.toUShort( reader.readShort() );

    if( 0 != completionCode )
      throw new BadCompletionCodeException( completionCode );

    results.clear();
    results.addAll( decodeWords( reader ));
    results.addAll( decodeBits( reader ));
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
      list.add( ( PlcBit ) Copier.with( proto, val ) );
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
        decodeWord( reader, proto);

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
  private PlcStruct decodeStruct( DataInput reader, PlcStruct proto ) throws IOException {
    var list = new ArrayList<IPlcWord>();
    var items = proto.items();
    var address = proto.address();

    for( var next : items ){
      var offset = next.address() - address;

      if( offset > 0 ){
        reader.skipBytes( 2 * offset );
      }

      var w = decodeWord( reader, next );
      address += getPointsCount( w ) + offset;

      list.add( w );
    }

    return Copier.with( proto, list );
  }
  /**
   *
   * @param reader
   * @param proto
   * @return
   */
  private IPlcWord decodeWord( DataInput reader, IPlcObject proto ) throws IOException {
    var value = Coder.decodeValue( reader, proto );

    return ( IPlcWord ) Copier.with( proto, value );
  }
  //endregion
}
