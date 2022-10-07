package melsec.types;

import melsec.bindings.PlcBinary;

public record PlcRegion( IDeviceCode device, int start, int size ) {

}
