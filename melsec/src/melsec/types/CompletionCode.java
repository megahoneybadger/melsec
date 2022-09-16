package melsec.types;

public enum CompletionCode {
  InvalidRange( 100 ),
  DecodingError( 200 ),
  InternalError( 500 );

  private int value;

  public int value(){
    return value;
  }

  CompletionCode( int v ){
    this.value = v;
  }

  public String toString(){
    return switch( this ){
      case InvalidRange -> "Invalid request range";
      case DecodingError -> "Request decoding error" ;
      default -> "Internal equipment error";
    };
  }

  public static CompletionCode getCompletionCode( int code ){
    for ( var c : CompletionCode.values()) {
      if( c.value() == code )
        return c;
    }

    return CompletionCode.InternalError;
  }
}
