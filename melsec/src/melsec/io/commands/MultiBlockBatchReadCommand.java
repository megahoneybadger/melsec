package melsec.io.commands;

import melsec.bindings.IPlcObject;
import melsec.bindings.IPlcWord;
import melsec.bindings.PlcBit;
import melsec.io.IOCompleteEventHandler;
import melsec.types.DataType;

import java.io.DataOutputStream;
import java.io.IOException;
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
  private List<PlcBit> bits;
  /**
   *
   */
  private List<IPlcWord> words;
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
      .map( x -> ( PlcBit )x )
      .collect( Collectors.toList() );

    words = StreamSupport
      .stream( targets.spliterator(), false )
      .filter( x -> x.type() != DataType.Bit )
      .map( x -> ( IPlcWord )x )
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
      var itemPoints = ( item instanceof IPlcWord w ) ? w.size() : 1;

      var shouldCreateCommand =
        ( blocks >= MAX_BLOCKS ) ||
          ( points + itemPoints > MAX_POINTS );

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

  //region Class 'Coding' methods
  /**
   *
   * @param writer
   * @throws IOException
   */
  @Override
  protected void encode( DataOutputStream writer ) throws IOException {
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
  }
  /**
   *
   * @param writer
   * @param list
   * @throws IOException
   */
  private void encode( DataOutputStream writer, Iterable<IPlcObject> list ) throws IOException {
    for( var item: list ){
      // Word device number
      writer.write( Coder.toBytes( item.address(), 3 ) );

      // Device code
      writer.write( item.device().getValue() );

      // Number of device points
      var size = ( item instanceof IPlcWord w ) ? w.size() : 1;
      writer.write( Coder.toBytes( size, 2 ) );
    }
  }
  //endregion
}
