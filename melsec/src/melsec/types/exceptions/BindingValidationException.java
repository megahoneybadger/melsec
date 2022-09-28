package melsec.types.exceptions;

import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class BindingValidationException extends BaseException {
  public BindingValidationException( String message, Object ... args ){

    super( ErrorCode.BadValidation, "Failed to validate bindings. " +
      MessageFormat.format( message, args ));

  }
}
