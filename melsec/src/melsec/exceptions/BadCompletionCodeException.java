package melsec.exceptions;

import melsec.commands.ICommand;
import melsec.types.ErrorCode;

import java.text.MessageFormat;

public class BadCompletionCodeException extends BaseException {
  public BadCompletionCodeException( int code ){

    super( ErrorCode.BadCompletionCode, MessageFormat.format(
      "Received bad completion code {0}", code ) );

  }
}
