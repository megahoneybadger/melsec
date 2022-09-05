package melsec.exceptions;

import melsec.types.ErrorCode;

public class DriverNotRunningException extends BaseException {
  public DriverNotRunningException(){
    super( ErrorCode.DriverNotRunning, "Driver is not running" );
  }
}
