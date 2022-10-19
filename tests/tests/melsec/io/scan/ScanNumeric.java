package melsec.io.scan;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcU2;
import melsec.bindings.PlcU4;
import melsec.bindings.files.BindingDeserializer;
import melsec.bindings.files.BindingSerializer;
import melsec.io.BaseIOTest;
import melsec.scanner.EquipmentScanner;
import melsec.simulation.Memory;
import melsec.types.BitDeviceCode;
import melsec.types.PlcRegion;
import melsec.types.WordDeviceCode;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.exceptions.BindingSerializationException;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.Copier;
import melsec.utils.RandomFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ScanNumeric extends BaseScanTest {

  //region Class 'Numeric' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
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

