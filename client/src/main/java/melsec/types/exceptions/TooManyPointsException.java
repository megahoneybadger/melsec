package melsec.types.exceptions;

import java.io.IOException;

public class TooManyPointsException extends IOException {
  public TooManyPointsException(){
    super( "Too many points" );
  }
}
