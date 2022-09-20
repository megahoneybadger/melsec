package melsec.simulation.handlers;

import melsec.exceptions.InvalidRangeException;
import melsec.simulation.Memory;

import java.io.DataInput;
import java.io.IOException;

public abstract class BaseHandler {

  //region Class members
  protected DataInput reader;
  protected Memory memory;
  //endregion

  //region Class initialization
  /**
   *
   * @param r
   */
  public BaseHandler(Memory m, DataInput r ){
    reader = r;
    memory = m;
  }
  //endregion

  //region Class 'Handle' methods

  /**
   *
   * @return
   * @throws IOException
   * @throws InvalidRangeException
   */
  public abstract byte[] handle() throws IOException, InvalidRangeException;
  //endregion
}
