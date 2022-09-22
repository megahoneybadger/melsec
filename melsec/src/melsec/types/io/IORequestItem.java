package melsec.types.io;

import melsec.bindings.IPlcObject;
import melsec.utils.Stringer;

import java.text.MessageFormat;

public record IORequestItem(IOType type, IPlcObject object ) {

  //region Class public methods
  public IOResponseItem toResponse(IPlcObject o ){
    return new IOResponseItem( type, object, IOResult.create( o ) );
  }

  public IOResponseItem toResponse( Throwable e ){
    return new IOResponseItem( type, object, IOResult.create( e ) );
  }

  /**
   *
   * @return
   */
  @Override
  public String toString(){
    return Stringer.toString( this );
  }
  //endregion
}