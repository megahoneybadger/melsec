package melsec.bindings;

import java.text.MessageFormat;

public class InvalidNumberException extends Exception {
  public InvalidNumberException(Number n, String target ){
    super( MessageFormat.format( "Invalid number value [{0}] for {1}.", n, target ) );
  }
}

