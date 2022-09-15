package melsec.simulation;

public class Equipment {

  //region Class members
  /**
   *
   */
  private Memory memory;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public Memory getMemory() {
    return memory;
  }
  //endregion

  //region Class initialization
  /**
   *
   */
  public Equipment(){
    memory = new Memory();
  }
  //endregion
}
