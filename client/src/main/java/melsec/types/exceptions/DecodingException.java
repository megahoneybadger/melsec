package melsec.types.exceptions;

import melsec.commands.ICommand;
import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class DecodingException extends BaseException {
  public DecodingException(ICommand command, Throwable e ){

    super( ErrorCode.InvalidDecoding, MessageFormat.format( "Failed to decode {0}. {1}",
      command.toString(), e.getMessage() ) );

  }
}
