package melsec.io.scan;

import melsec.types.BitDeviceCode;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class ScanString extends BaseScanTest {

  //region Class 'Bit' methods

  @Test
  public void Should_Scan_Strings_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcString( 1, 20, 10  ));

    var f = createScanFrame( getWordRegions(), prototypes, 10 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Strings_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcString( 1, 20, 1000  ));

    var f = createScanFrame( getWordRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Strings_3() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcString( 1, 20, 5000  ));

    var f = createScanFrame( getWordRegions(), prototypes, 150 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Strings_4() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcString( 1, 10, 10000  ));

    var f = createScanFrame( getWordRegions(), prototypes, 200 );

    f.await();

    f.assertScanResults( );
  }

  //endregion

}

