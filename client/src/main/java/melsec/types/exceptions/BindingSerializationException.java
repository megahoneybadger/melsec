package melsec.types.exceptions;

import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class BindingSerializationException extends BaseException {
  public BindingSerializationException( String message, Object ... args ){

    super( ErrorCode.InvalidSerialization, "Failed to serialize bindings. " +
      MessageFormat.format( message, args ));

  }
}
