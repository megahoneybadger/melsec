package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.PlcCoordinate;

import java.text.MessageFormat;

public interface IPlcObject {

  String EMPTY_STRING = "";

  IDeviceCode device();

  int address();

  String id();

  DataType type();
}
