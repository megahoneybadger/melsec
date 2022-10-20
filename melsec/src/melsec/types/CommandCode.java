package melsec.types;

public enum CommandCode {
  BatchRead( 0x0401 ),
  BatchWrite( 0x1401 ),

  MultiBlockBatchRead( 0x0406 ),
  MultiBlockBatchWrite( 0x1406 ),

  RandomWrite( 0x1402 );

  private int value;

  public int value(){
    return value;
  }

  CommandCode( int v ){
    this.value = v;
  }

  public static CommandCode fromInt( int v ) {
    for (var type : values()) {
      if ( type.value() == v) {
        return type;
      }
    }
    return null;
  }
}

