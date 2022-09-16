package melsec.simulation.handlers;

import melsec.types.CompletionCode;
import melsec.utils.ByteConverter;
import melsec.utils.Coder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ErrorHandler extends BaseHandler {

  //region Class members
  /**
   *
   */
  private CompletionCode code;
  //endregion

  //region Class initialization
  /**
   *
   */
  public ErrorHandler(CompletionCode c){
    super( null, null );

    code = c;
  }
  //endregion

  //region Class 'Handle' methods
  @Override
  public byte[] handle() {
    try( var bs = new ByteArrayOutputStream()){
      try( var w = new DataOutputStream( bs )){

        Coder.encodeTitle( w );

        w.write( ByteConverter.toBytes( 2, 2 )  );

        var iCompletionCode = ( short )code.value();
        w.write( ByteConverter.toBytes( iCompletionCode, 2 ) );

        return bs.toByteArray();
      }
    }
    catch( Exception e ){

    }

    return ByteConverter.empty();
  }

  //endregion
}
