package melsec.types;

public enum ErrorCode {
  DriverNotRunning,
  ConnectionNotEstablished,

  InvalidEncoding,
  InvalidDecoding,
  BadCompletionCode,

  InvalidRange,

  InvalidDeserialization,
  InvalidSerialization,
}
