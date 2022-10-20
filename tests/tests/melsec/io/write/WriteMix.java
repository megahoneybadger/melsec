package melsec.io.write;

import melsec.bindings.PlcBit;
import melsec.io.BaseIOTest;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import melsec.utils.UtilityHelper;
import melsec.utils.Valuer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteMix extends BaseIOTest {

  //region Class 'Bit' methods

  @Test
  public void Should_Write_MultipleBitAndNumeric_1() throws InvalidRangeException, InterruptedException {

    var toWrite = UtilityHelper.unionShuffle(
      RandomFactory.getPlcBit( 10 ),
      RandomFactory.getPlcNumerics( 10 ));

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBitAndNumeric_2() throws InvalidRangeException, InterruptedException {

    var toWrite = UtilityHelper.unionShuffle(
      RandomFactory.getPlcBit( 1000 ),
      RandomFactory.getPlcNumerics( 1000 ));

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBitAndNumeric_3() throws InvalidRangeException, InterruptedException {

    var toWrite = UtilityHelper.unionShuffle(
      RandomFactory.getPlcBit( 100000 ),
      RandomFactory.getPlcNumerics( 10000 ));

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBitAndNumeric_4() throws InvalidRangeException, InterruptedException {

    var toWrite = UtilityHelper.union(
      RandomFactory.getPlcBit( 100000 ),
      RandomFactory.getPlcNumerics( 10000 ));

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }

  @Test
  public void Should_Write_MultipleBitAndNumeric_5() throws InvalidRangeException, InterruptedException {

    var toWrite = UtilityHelper.union(
      RandomFactory.getPlcBit( 150000 ),
      RandomFactory.getPlcNumerics( 20000 ));

    var f = createFrame();
    f.writeAsync( toWrite );

    f.await();

    f.assertWriteResults( toWrite );
  }
  //endregion
}

