package melsec.types.events;

import melsec.types.io.IOResponse;

public interface IOCompleteEvent {
  void complete( IOResponse response );
}
