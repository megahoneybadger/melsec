package melsec.io;

import melsec.bindings.*;

import java.util.ArrayList;
import java.util.List;

public class IORequest {
  private List<IORequestItem> list = new ArrayList<>();

  public int count(){
    return list.size();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder{

    private List<IORequestItem> list = new ArrayList<>();

    public Builder read( IPlcObject o ){
      list.add( new IORequestItem( IOType.Read, o ) );

      return this;
    }

    public Builder write( IPlcObject o ){
      list.add( new IORequestItem( IOType.Write, o ) );

      return this;
    }



    public IORequest build(){
      var res = new IORequest();
      res.list = new ArrayList<IORequestItem>( list );

      return res;
    }

  }
}




