package melsec.utils;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcU2;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static melsec.simulation.Memory.*;

public class RandomFactory {

  //region Class members
  /**
   *
   */
  private static Random random = new Random();
  //endregion

  //region Class 'Numeric' methods

  /**
   * @return
   */
  public static int getU2() {
    return random.nextInt(0, 0xFFFF);
  }
  //endregion

  //region Class 'Plc Object' methods

  /**
   * @return
   */
  public static PlcU2 getPlcU2() {
    var device = getWordDeviceCode();
    var address = random.nextInt(0, MAX_WORDS);
    var value = getU2();

    return new PlcU2(device, address, value);
  }

  /**
   * @param count
   * @return
   */
  public static List<IPlcObject> getPlcU2(int count) {
    var list = new ArrayList<IPlcObject>();
    var set = new HashSet<FullAddress>();

    for(int i = 0; i < count; ++i) {
      FullAddress fa;
      PlcU2 next;

      do{
        next = getPlcU2();
        fa = new FullAddress( next.device(), next.address() );
      }
      while( set.contains( fa ) );

      list.add( next );
      set.add( fa );
    }

    return list;
  }

  /**
   * @return
   */
  public static WordDeviceCode getWordDeviceCode() {
    var values = WordDeviceCode.values();
    var index = random.nextInt(0, values.length);
    return values[index];
  }

  /**
   * @return
   */
  public static BitDeviceCode getBitDeviceCode() {
    var values = BitDeviceCode.values();
    var index = random.nextInt(0, values.length);
    return values[index];
  }
  //endregion

  //region Class internal structs

  /**
   * @param device
   * @param address
   */
  record FullAddress(IDeviceCode device, int address) {
  }
  //endregion
}

