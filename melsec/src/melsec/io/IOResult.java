package melsec.io;

import melsec.bindings.IPlcObject;

public class IOResult {
  private IOErrorCode error = IOErrorCode.None;
  private IPlcObject value;

  public boolean success(){
    return error == IOErrorCode.None;
  }

  public boolean failure(){
    return !success();
  }

  public IOErrorCode error(){
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

  public static IOResult create( IOErrorCode code ){
    var res = new IOResult();
    res.error = code;
    return res;
  }

}
