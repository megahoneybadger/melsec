package melsec.io;

import melsec.utils.UtilityHelper;

public record IORequestUnit( IOType operation,
                             Iterable<IORequestItem> items,
                             IOCompleteEventHandler handler) {

  public IORequestUnit with( Iterable<IORequestItem> items ){
    return new IORequestUnit( operation, UtilityHelper.toList( items ), handler );
  }
}