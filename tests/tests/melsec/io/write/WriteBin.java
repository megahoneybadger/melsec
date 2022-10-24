package melsec.io.write;

import melsec.bindings.PlcBinary;
import melsec.io.BaseIOTest;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import org.junit.jupiter.api.Test;

public class WriteBin extends BaseIOTest {

  //region Class 'String' methods

//  @Test
//  public void Should_Write_Binary_1() throws InvalidRangeException, InterruptedException {
//    var toWrite = new PlcBinary( WordDeviceCode.W, 0, 3, new byte[]{ 1, 2, 3 } );
//
//    var f = createFrame();
//    f.writeAsync( toWrite );
//
//    f.await();
//
//    f.assertWriteResults( toWrite );
//  }
  //endregion
}

