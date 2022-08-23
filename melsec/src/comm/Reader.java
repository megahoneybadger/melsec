package comm;

import melsec.bindings.IPlcObject;
import types.CommandCode;
import types.OperationHandler;
import types.OperationLog;
import types.OperationResult;

import java.util.Arrays;

public class Reader {

  public void add( IPlcObject obj, OperationHandler handler ){

    handler.completed( obj, new OperationLog( obj, CommandCode.MultiBlockBatchRead, 10 ));
  }

//  public void add( Iterable<IPlcObject> list ){
//
//  }
//
//  public void add( IPlcObject... col ){
//    add( Arrays.stream(col).toList() );
//  }
}
