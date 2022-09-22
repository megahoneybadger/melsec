package melsec.types.io;

import melsec.bindings.IPlcObject;
import melsec.utils.Stringer;

public record IOResponseItem(IOType operation, IPlcObject proto, IOResult result ) {

  //region Class public methods

  /**
   *
   * @return
   */
  public String toString(){
    return Stringer.toString( this );
  }
  //endregion
}
