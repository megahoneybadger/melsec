package melsec.io;

import melsec.bindings.IPlcObject;

public record IORequestItem(IOType type, IPlcObject object ) {

  //region Class public methods
  public IOResponseItem toResponse(IPlcObject o ){
    return new IOResponseItem( type, object, IOResult.create( o ) );
  }

  public IOResponseItem toResponse( Throwable e ){
    return new IOResponseItem( type, object, IOResult.create( e ) );
  }
  //endregion
}