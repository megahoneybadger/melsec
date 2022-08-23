package types;

import melsec.bindings.IPlcObject;

public interface OperationHandler {
  void completed( IPlcObject result, OperationLog log );

  void failed( ErrorCode error, OperationLog log );
}
