package melsec.types.events;

public enum EventType {
  ClientStarted,
  ClientStopped,

  ConnectionConnecting,
  ConnectionEstablished,
  ConnectionDropped,
  ConnectionDisposed,

  CommandBeforeSend,
  CommandAfterSend,
}
