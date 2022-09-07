package melsec.events.net;

import melsec.events.IEventArgs;
import melsec.net.Endpoint;

public record ConnectionEventArgs( Endpoint endpoint, String id ) implements IEventArgs {

  @Override
  public String toString(){
    return endpoint.toString();
  }
}
