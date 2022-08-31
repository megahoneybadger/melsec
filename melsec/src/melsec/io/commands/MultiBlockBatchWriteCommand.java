package melsec.io.commands;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.io.IOCompleteEventHandler;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MultiBlockBatchWriteCommand extends ICommand {

  @Override
  public CommandCode code() {
    return CommandCode.MultiBlockBatchWrite;
  }

  public byte [] encode(){
    return null;
  }

  public static List<MultiBlockBatchReadCommand> split(
    Iterable<IPlcObject> items, IOCompleteEventHandler handler ){
    var list = new ArrayList<MultiBlockBatchReadCommand>();

    var stream = StreamSupport.stream( items.spliterator(), false);

    stream
      .filter( x -> x instanceof PlcBit)
      .collect( Collectors.toList() );

    //items.spliterator().


    return list;
  }

  //region Class 'Encoding' methods
  /**
   *
   * @param ds
   * @throws IOException
   */
  @Override
  protected void encode(DataOutput ds) throws IOException {

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
