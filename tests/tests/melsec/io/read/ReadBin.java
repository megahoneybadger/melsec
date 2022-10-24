package melsec.io.read;

import melsec.bindings.PlcBinary;
import melsec.bindings.PlcString;
import melsec.io.BaseIOTest;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReadBin extends BaseIOTest {

  //region Class 'Bin' methods

//  @Test
//  public void Should_Read_Bin_1() throws InvalidRangeException, InterruptedException {
//    var toWrite = new PlcBinary( WordDeviceCode.W, 0, 3, new byte[]{ 1, 2, 3, 4, 5 } );
//    server.write(toWrite);
//
//    var toRead = Copier.withoutValue( toWrite );
//
//    var f = createFrame();
//    f.readAsync( toRead );
//
//    f.await();
//
//    f.assertReadResults( toWrite );
//  }
//
//  @Test
//  public void Should_Read_Bin_2() throws InvalidRangeException, InterruptedException {
//    var toWrite = new PlcBinary( WordDeviceCode.W, 0, SIZE_ODD, new byte[]{ 1, 2, 3 } );
//    server.write(toWrite);
//
//    var toRead = Copier.withoutValue( toWrite );
//
//    var f = createFrame();
//    f.readAsync( toRead );
//
//    f.await();
//
//    f.assertReadResults( toWrite );
//  }
//
//  @Test
//  public void Should_Read_Bin_4() throws InvalidRangeException, InterruptedException {
//    var toWrite = new PlcBinary( WordDeviceCode.W, 0, SIZE_EVEN, new byte[]{ 1, 2, 3 } );
//    server.write(toWrite);
//
//    var toRead = Copier.withoutValue( toWrite );
//
//    var f = createFrame();
//    f.readAsync( toRead );
//
//    f.await();
//
//    f.assertReadResults( toWrite );
//  }
  //endregion
}

