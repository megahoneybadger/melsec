package melsec.bindings;

import melsec.exceptions.DecodingException;
import melsec.utils.EndianDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInput;

public class BaseTest {
  //region Class constants
  protected static final String NAME_1 = "plc_object_1";
  protected static final String NAME_2 = "plc_object_2";
  protected static final String NAME_3 = "plc_object_3";

  protected static final int ADDRESS_1 = 10;
  protected static final int ADDRESS_2 = 200;
  protected static final int ADDRESS_3 = 1000;

  protected static final int SIZE_1 = 10;
  protected static final int SIZE_2 = 20;
  protected static final int SIZE_3 = 30;

  protected static final String STRING_1 = "Very long string";
  protected static final String STRING_2 = "Hello world, New York";
  protected static final String STRING_3 = "One two three";
  //endregion

  //region Class 'Coding' methods
  protected DataInput getInputStream(byte [] buffer ) {
    try( var bs = new ByteArrayInputStream( buffer )){
      try( var ds = new EndianDataInputStream( bs )){
        return ds;
      }
    }
    catch( Exception e ){

    }

    return null;
  }
  //endregion

}
