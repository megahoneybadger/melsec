package melsec.io.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.io.IOCompleteEventHandler;
import melsec.io.IORequestItem;
import melsec.io.IORequestUnit;
import melsec.io.commands.CommandCode;
import melsec.io.commands.ICommand;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
  //endregion

  //region Class 'Encoding' methods
  /**
   *
   * @param ds
   * @throws IOException
   */
  @Override
  protected void encode( DataOutput ds ) throws IOException {
    throw new IOException( "suck my dick" );
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
