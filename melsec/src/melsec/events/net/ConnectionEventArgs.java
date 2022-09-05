package melsec.events.net;

import melsec.net.Endpoint;
import melsec.events.IEventArgs;

public record ConnectionEventArgs( Endpoint endpoint, String id ) implements IEventArgs {

  @Override
  public String toString(){
    return endpoint.toString();
  }
}
