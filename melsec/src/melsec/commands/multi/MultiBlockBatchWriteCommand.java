package melsec.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.commands.ICommand;
import melsec.io.IORequestItem;
import melsec.io.IORequestUnit;
import melsec.commands.CommandCode;
import melsec.types.DataType;
import melsec.utils.Coder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiBlockBatchWriteCommand extends MultiBlockBatchBaseCommand {

  //region Class properties
  /**
   *
   * @return
   */
  @Override
  public CommandCode code() {
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

      var shouldCreateCommand =
        ( blocks >= MAX_BLOCKS ) || ( points + itemPoints > MAX_POINTS );

      if( shouldCreateCommand ){
        res.add( new MultiBlockBatchWriteCommand( unit.with( items ) ) );
        items.clear();
        blocks = 0;
        points = 0;
      }

      blocks++;
      points += itemPoints;
      items.add( item );
    }

    if( items.size() > 0 ){
      res.add( new MultiBlockBatchWriteCommand( unit.with( items ) ) );
    }

    return res;
  }
  /**
   *
   * @return
   */
  @Override
  public ICommand copy(){
    var copy = new MultiBlockBatchWriteCommand( unit );
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
    int wordSize = 0;

    for( var x: words ) {
      wordSize += ( 3 + 1 + 2 + getPointsCount() );
    }

    int iBitsSize = bits.size() * 8 /* 3 + 1 + 2 + 2*/;
    int iTotalSize = 2 + 6 + wordSize + iBitsSize;

    Coder.encodeHeader( writer, iTotalSize );

    // Command
    writer.writeByte( 0x06 );
    writer.writeByte( 0x14 );

    // Subcommand 00 means use children units (bit packed in word bits)
    writer.writeByte( 0x00 );
    writer.writeByte( 0x00 );

    // Number of word device blocks
    writer.writeByte( words.size() );

    // Number of bit device blocks
    writer.writeByte( bits.size() );

    encodeBlock( writer, words );
    encodeBlock( writer, bits );
  }
  /**
   *
   * @param writer
   * @param list
   * @throws IOException
   */
  private void encodeBlock( DataOutput writer, List<IPlcObject> list ) throws IOException {
    for( var o: list ) {
      // Word device number
      var arr = Coder.toBytes( o.address(), 3 );
      writer.write( arr );

      // Device code
      var btCode = ( byte )o.device().value();
      writer.write( btCode );

      arr = Coder.toBytes( getPointsCount( o ), 2 );
      writer.write( arr );

      if( o.type() == DataType.Bit ){
        short value = ( short )( (( PlcBit )o ).value() ? 1 : 0 );
        arr = Coder.toBytes( value, 2 );
      } else {

      }

      writer.write( arr );
    }
  }
  //endregion

  //region Class 'Decoding' methods
  /**
   *
   * @param reader
   */
  @Override
  protected void decode( DataInput reader ){

  }
  //endregion
}
