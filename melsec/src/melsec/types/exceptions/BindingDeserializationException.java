package melsec.types.exceptions;

import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class BindingDeserializationException extends BaseException {
  public BindingDeserializationException( String message, Object ... args ){

    super( ErrorCode.InvalidDeserialization, "Failed to deserialize bindings. " +
      MessageFormat.format( message, args ));

  }
}
