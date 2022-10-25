package melsec.types.events.scanner;

import melsec.bindings.IPlcObject;
import melsec.types.events.IEventArgs;

import java.util.List;

public record ScannerEventArgs( List<IPlcObject> changes ) implements IEventArgs {

}
