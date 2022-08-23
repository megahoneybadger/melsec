package melsec.bindings;

import types.DataType;
import types.IDeviceCode;

public interface IPlcObject {

  String EMPTY_STRING = "";

  IDeviceCode device();

  int address();

  String id();

  DataType type();
}
