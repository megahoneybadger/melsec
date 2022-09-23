package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcI2;
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


public class ReadNumeric extends BaseIOTest {

  //region Class 'Numeric' methods
  /**
   * @throws IOException
   * @throws InvalidRangeException
   * @throws InterruptedException
   */
  @Test
  public void Should_Read_Numeric_1() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcNumerics( 10 );

    server.write(toWrite);

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }


  @Test
  public void Should_Read_Numeric_2() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcNumerics( 100 );

    server.write(toWrite);

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_Numeric_3() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcNumerics( 1000 );

    server.write(toWrite);

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_Numeric_4() throws InvalidRangeException, InterruptedException {
    var toWrite = RandomFactory.getPlcNumerics( 10000 );

    server.write(toWrite);

    var toRead = Copier.without( toWrite );

    var f = createFrame();
    f.readAsync( toRead );

    f.await();

    f.assertResults( toWrite );
  }

  @Test
  public void Should_Read_Numeric_5() throws InvalidRangeException, InterruptedException {
    for( int i = 0; i < 10; ++i ){
      Should_Read_Numeric_4();
    }
  }
  //endregion
}

