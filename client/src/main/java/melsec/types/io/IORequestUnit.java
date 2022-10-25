package melsec.types.io;

import melsec.types.events.IOCompleteEvent;
import melsec.utils.UtilityHelper;

import java.util.Collections;

public record IORequestUnit( IOType operation,
                             Iterable<IORequestItem> items,
                             IOCompleteEvent handler ) {

  public IORequestUnit with( Iterable<IORequestItem> items ){
    return new IORequestUnit( operation, UtilityHelper.toList( items ), handler );
  }

  public IORequestUnit with( IORequestItem item ){
    return new IORequestUnit( operation, Collections.singletonList( item ), handler );
  }
}