package melsec.bindings;

import melsec.bindings.DeviceCode;
import java.text.MessageFormat;

public class InvalidDeviceCodeException extends Exception {
  public InvalidDeviceCodeException( DeviceCode device ){
    super( MessageFormat.format( "Invalid device code [{0}] for the entity.", device ) );
  }
}

