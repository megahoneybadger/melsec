package melsec.types;

import melsec.bindings.IPlcObject;

public record OperationLog( IPlcObject target, CommandCode command, long duration ) {
}
