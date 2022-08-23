package types;

import melsec.bindings.IPlcObject;

public record OperationResult( boolean success, CommandCode command,
                               IPlcObject result, ErrorCode error ) {
  boolean failure(){
    return !this.success;
  }

  public OperationResult( CommandCode command, IPlcObject result ){
    this( true, command, result, ErrorCode.None );
  }

  public OperationResult( CommandCode command, ErrorCode error ){
    this( false, command, null, error );
  }

  public static OperationResult error( CommandCode command, ErrorCode err ){
    return new OperationResult( command, err );
  }

  public static OperationResult ok( CommandCode command, IPlcObject result ){
    return new OperationResult( command, result );
  }
}
