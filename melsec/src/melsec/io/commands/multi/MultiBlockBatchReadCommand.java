package melsec.io.commands.multi;

import melsec.bindings.*;
import melsec.io.IORequestItem;
import melsec.io.IORequestUnit;
import melsec.io.IOResponse;
import melsec.io.IOResponseItem;
import melsec.io.commands.Coder;
import melsec.io.commands.CommandCode;
import melsec.io.commands.ICommand;
import melsec.types.DataType;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
  public static List<ICommand> split( IORequestUnit unit ){
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

    //throw  new IOException( "suck my dick" );

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
