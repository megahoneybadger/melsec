package melsec.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.commands.ICommand;
import melsec.exceptions.BadCompletionCodeException;
import melsec.io.IORequestItem;
import melsec.io.IORequestUnit;
import melsec.commands.CommandCode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiBlockBatchWriteCommand extends MultiBlockBatchBaseCommand {

  //region Class constants
  /**
   *
   */
  protected final int HEADER_LENGTH = 9;
  //endregion

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
   * @param w
   * @throws IOException
   */
  @Override
  protected void encode( DataOutput w ) throws IOException {
    int wordSize = 0;

    for( var x: words ) {
      wordSize += ( 3 + 1 + 2 + getPointsCount( x ) * 2 );
    }

    int iBitsSize = bits.size() * 8 /* 3 + 1 + 2 + 2*/;
    int iTotalSize = 2 + 6 + wordSize + iBitsSize;

    encodeHeader( w, iTotalSize );

    // Command
    w.writeByte( 0x06 );
    w.writeByte( 0x14 );

    // Subcommand 00 means use children units (bit packed in word bits)
    w.writeByte( 0x00 );
    w.writeByte( 0x00 );

    // Number of word device blocks
    w.writeByte( words.size() );

    // Number of bit device blocks
    w.writeByte( bits.size() );

    encodeBlocks( w, words );
    encodeBlocks( w, bits );
  }
  /**
   *
   * @param w
   * @param list
   * @throws IOException
   */
  private void encodeBlocks( DataOutput w, List<IPlcObject> list ) throws IOException {
    for( var o: list ) {
      // Word device number
      var arr = toBytes( o.address(), 3 );
      w.write( arr );

      // Device code
      var btCode = ( byte )o.device().value();
      w.write( btCode );

      // Number of device points
      arr = toBytes( getPointsCount( o ), 2 );
      w.write( arr );

      encode( w, o );
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
      .collect( Collectors.toList());
  }

  //endregion
}

// write w100 u2 100, w101 i2 200
