package melsec.types;

import melsec.bindings.IPlcObject;
import melsec.io.IOErrorCode;

public record OperationResult( boolean success, CommandCode command,
                               IPlcObject result, IOErrorCode error ) {
  boolean failure(){
    return !this.success;
  }

  public OperationResult( CommandCode command, IPlcObject result ){
    this( true, command, result, IOErrorCode.None );
  }

  public OperationResult( CommandCode command, IOErrorCode error ){
    this( false, command, null, error );
  }

  public static OperationResult error( CommandCode command, IOErrorCode err ){
    return new OperationResult( command, err );
  }

  public static OperationResult ok( CommandCode command, IPlcObject result ){
    return new OperationResult( command, result );
  }
}
