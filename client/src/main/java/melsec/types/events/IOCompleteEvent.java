package melsec.types.events;

import melsec.types.exceptions.InvalidRangeException;
import melsec.types.io.IOResponse;

public interface IOCompleteEvent {
  void complete( IOResponse response ) throws InvalidRangeException;
}
