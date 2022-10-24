package melsec.io.scan;

import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.Test;


public class ScanStruct extends BaseScanTest {

  //region Class 'Bit' methods

  @Test
  public void Should_Scan_Structs_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcStruct( 10, 10 ));

    var f = createScanFrame( getWordRegions(), prototypes, 10 );

    f.await();

    f.assertScanResults();
  }

  @Test
  public void Should_Scan_Structs_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcStruct( 10, 1000 ));

    var f = createScanFrame( getWordRegions(), prototypes, 50 );

    f.await();

    f.assertScanResults();
  }

  @Test
  public void Should_Scan_Structs_3() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcStruct( 7, 5000 ));

    var f = createScanFrame( getWordRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults();
  }

  //endregion

}

