package melsec.commands.batch;

import melsec.types.CommandCode;
import melsec.commands.ICommand;
import melsec.types.io.IOResponse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BatchWriteCommand extends ICommand {
  @Override
  public CommandCode code() {
    return CommandCode.BatchWrite;
  }

  //region Class 'Coding' methods
  /**
   *
   * @param ds
   * @throws IOException
   */
  @Override
  protected void encode(DataOutput ds) throws IOException {

  }
  @Override
  protected void decode( DataInput reader ){

  }
  /**
   *
   * @return
   */
  @Override
  public ICommand copy(){
    return null;
  }
  /**
   *
   * @return
   */
  public IOResponse toResponse(Throwable e){
    return null;
  }
  //endregion
}
