package melsec.bindings;

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

  protected static final int INVALID_ADDRESS_1 = 100000;
  protected static final int INVALID_ADDRESS_2 = 200000;
  protected static final int INVALID_ADDRESS_3 = 300000;

  protected static final int SIZE_1 = 10;
  protected static final int SIZE_2 = 20;
  protected static final int SIZE_3 = 30;
  protected static final int SIZE_ODD = 5;
  protected static final int SIZE_EVEN = 4;

  protected static final String STRING_1 = "Very long string";
  protected static final String STRING_2 = "Hello world, New York";
  protected static final String STRING_3 = "One two three";

  protected static final String ERROR_INVALID_RANGE = "invalid request range";
  protected static final String ERROR_CLOSED_CONNECTION = "An existing connection was forcibly closed by the remote host";
  protected static final String ERROR_INVALID_ADDRESS = "Invalid device address";
  protected static final String ERROR_TOO_MANY_POINTS = "Too many points";


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
