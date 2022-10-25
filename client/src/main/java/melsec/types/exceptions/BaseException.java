package melsec.types.exceptions;

import melsec.commands.ICommand;
import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class BaseException extends Exception {

  private ErrorCode code;

  public ErrorCode getCode(){
    return code;
  }

  public BaseException( ErrorCode code, String message ){
    super( message );

    this.code = code;
  }
}
