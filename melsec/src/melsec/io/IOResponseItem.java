package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.utils.Stringer;

public record IOResponseItem( IOType operation, IPlcObject target, IOResult result ) {

  //region Class public methods
  public String toString(){
    return Stringer.toString( this );
  }
  //endregion
}
