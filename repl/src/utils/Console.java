package utils;

import melsec.bindings.IPlcNumber;
import melsec.bindings.IPlcObject;
import melsec.bindings.PlcBit;
import melsec.io.IOResponseItem;

import java.text.MessageFormat;

public class Console {

  //region Class 'Print' methods
  /**
   *
   * @param item
   */
  public static void print(IOResponseItem item ){
    if( item.result().failure() ){
      System.out.print(Color.YELLOW);
      System.out.print( Color.RED_BACKGROUND_BRIGHT );
      System.out.println( item );
      System.out.print( Color.RESET );

    } else {
      System.out.print( Color.WHITE );
      System.out.print( Color.GREEN_BACKGROUND_BRIGHT );
      System.out.println(item);
      System.out.print(Color.RESET);
    }
  }
  /**
   *
   * @param e
   */
  public static void error( Throwable e ){
    System.out.print(Color.WHITE );
    System.out.print( Color.RED_BACKGROUND_BRIGHT );
    System.out.println( e.getMessage() );
    System.out.print( Color.RESET );
  }
  //endregion
}
