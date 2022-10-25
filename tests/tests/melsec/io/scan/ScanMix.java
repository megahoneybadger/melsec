package melsec.io.scan;

import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.Test;


public class ScanMix extends BaseScanTest {

  //region Class 'Bits+Numeric' methods

  @Test
  public void Should_Scan_BitsAndNumeric_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 10 ),
      RandomFactory.getPlcNumerics( 10 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_BitsAndNumeric_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 5000 ),
      RandomFactory.getPlcNumerics( 1000 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_BitsAndNumeric_3() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 50000 ),
      RandomFactory.getPlcNumerics( 10000 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 150 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_BitsAndNumeric_4() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 80000 ),
      RandomFactory.getPlcNumerics( 25000 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 200 );

    f.await();

    f.assertScanResults( );
  }
  //endregion

  //region Class 'Bits+Numeric+String'
  @Test
  public void Should_Scan_BitsAndNumericAndString_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 10 ),
      RandomFactory.getPlcWords( 10 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_BitsAndNumericAndString_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 5000 ),
      RandomFactory.getPlcWords( 1000 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 50 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_BitsAndNumericAndString_3() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcBit( 50000 ),
      RandomFactory.getPlcWords( 10000 ) );

    var f = createScanFrame( getAllRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }
  //endregion

}

