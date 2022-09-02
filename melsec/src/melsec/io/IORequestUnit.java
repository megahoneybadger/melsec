package melsec.io;

import melsec.utils.UtilityHelper;

public record IORequestUnit( IOType type,
                             Iterable<IORequestItem> items,
                             IOCompleteEventHandler handler) {

  public IORequestUnit with( Iterable<IORequestItem> items ){
    return new IORequestUnit( type, UtilityHelper.toList( items ), handler);
  }
}