package melsec.types.io;

import melsec.bindings.IPlcObject;

public class IOResult {
  private Throwable error;
  private IPlcObject value;

  public boolean success(){
    return error == null;
  }

  public boolean failure(){
    return !success();
  }

  public Throwable error(){
    return error;
  }

  public IPlcObject value(){
    return value;
  }

  public static IOResult create(){
    return new IOResult();
  }

  public static IOResult create( IPlcObject o ){
    var res = new IOResult();
    res.value = o;
    return res;
  }

  public static IOResult create( Throwable e ){
    var res = new IOResult();
    res.error = e;
    return res;
  }

}
