package melsec.io;

import melsec.bindings.IPlcObject;

public record IORequestItem( IOType type, IPlcObject object ) {

}