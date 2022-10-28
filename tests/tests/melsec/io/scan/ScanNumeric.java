package melsec.io.scan;

import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.exceptions.BindingSerializationException;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class ScanNumeric extends BaseScanTest {

  //region Class 'Numeric' methods
  /**
   */
  @Test
  public void Should_Scan_Numerics_1() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcNumerics( 10 ));

    var f = createScanFrame( getWordRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Numerics_2() throws InvalidRangeException, InterruptedException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcNumerics( 1000 ));

    var f = createScanFrame( getWordRegions(), prototypes, 100 );

    f.await();

    f.assertScanResults( );
  }

  @Test
  public void Should_Scan_Numerics_3() throws InvalidRangeException, InterruptedException, BindingSerializationException, BindingDeserializationException {

    var prototypes = Copier.withoutValue(
      RandomFactory.getPlcNumerics( 10000 ));

    //var path = "../.resources/conf_big/file10.xml";
    //BindingSerializer.write( "../.resources/conf_big/file10.xml", prototypes );
    //var prototypes = BindingDeserializer.read( path );

    var f = createScanFrame( getWordRegions(), prototypes, 200 );

    f.await();

    f.assertScanResults( );
  }
  //endregion
}

