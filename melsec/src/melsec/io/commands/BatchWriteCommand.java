package melsec.io.commands;

import java.io.DataOutputStream;
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
  protected void encode(DataOutputStream ds) throws IOException {

  }
  //endregion
}
