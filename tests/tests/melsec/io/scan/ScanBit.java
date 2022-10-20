package melsec.io.scan;

import melsec.io.BaseIOTest;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.PlcRegion;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.MemoryRandomUpdater;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ScanBit extends BaseScanTest {

  //region Class 'Bit' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Scan_Bits_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 10, BitDeviceCode.M ));

    var f = createScanFrame( getBitRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Bits_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 2000, BitDeviceCode.M ));

    var f = createScanFrame( getBitRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Bits_3() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 20000, BitDeviceCode.M ),
      RandomFactory.getPlcBit( 20000, BitDeviceCode.B ));

    var f = createScanFrame( getBitRegions(), prototypes, 300 );

    f.await();

    f.assertScanResults();
  }

  @Test
  public void Should_Scan_Bits_4() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 10000, BitDeviceCode.M ),
      RandomFactory.getPlcBit( 10000, BitDeviceCode.B ),
      RandomFactory.getPlcBit( 10000, BitDeviceCode.X ),
      RandomFactory.getPlcBit( 555, BitDeviceCode.Y ),
      RandomFactory.getPlcBit( 333, BitDeviceCode.L ));

    var f = createScanFrame( getBitRegions(), prototypes, 400 );

    f.await();

    f.assertScanResults();
  }

  @Test
  public void Should_Scan_Bits_5() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 50000 ));

    var f = createScanFrame( getBitRegions(), prototypes, 200 );

    f.await();

    f.assertScanResults();
  }
  //endregion

}

