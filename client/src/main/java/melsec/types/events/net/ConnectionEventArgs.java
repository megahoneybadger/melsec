package melsec.types.events.net;

import melsec.types.events.IEventArgs;
import melsec.types.Endpoint;

public record ConnectionEventArgs( Endpoint endpoint, String id ) implements IEventArgs {

  @Override
  public String toString(){
    return endpoint.toString();
  }
}
