package melsec.exceptions;

import melsec.io.commands.ICommand;

import java.text.MessageFormat;

public class EncodingException extends Exception {
  public EncodingException( ICommand command, Throwable e ){
    super( MessageFormat.format( "Failed to encode {0}. {1}", command.toString(), e.getMessage() ) );
  }
}
