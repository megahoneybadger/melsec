package melsec.exceptions;

public class DriverNotRunningException extends Exception {
  public DriverNotRunningException(){
    super( "driver is not running" );
  }
}
