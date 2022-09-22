package melsec.types.io;

import melsec.utils.UtilityHelper;

import java.util.ArrayList;

public record IOResponse( Iterable<IOResponseItem> items ) {

  public static IOResponse empty(){
    return new IOResponse( new ArrayList<>() );
  }

  public IOResponse merge( IOResponse r ){
    var listA = UtilityHelper.toList( items );
    var listB = UtilityHelper.toList( r.items );

    var list = new ArrayList<IOResponseItem>();
    list.addAll( listA );
    list.addAll( listB );

    return new IOResponse( list );
  }
}
