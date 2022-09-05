package melsec.exceptions;

import melsec.commands.ICommand;
import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class EncodingException extends BaseException {
  public EncodingException( ICommand command, Throwable e ){

    super( ErrorCode.InvalidEncoding, MessageFormat.format( "Failed to encode {0}. {1}",
      command.toString(), e.getMessage() ) );

  }
}
