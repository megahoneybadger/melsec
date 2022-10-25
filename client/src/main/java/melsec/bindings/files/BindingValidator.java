package melsec.bindings.files;

import melsec.bindings.*;
import melsec.types.DataType;
import melsec.types.PlcCoordinate;
import melsec.types.exceptions.BindingDeserializationException;
import melsec.types.exceptions.BindingValidationException;
import melsec.utils.Stringer;
import melsec.utils.UtilityHelper;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;

public class BindingValidator {

  //region Class 'File' methods
  /**
   *
   * @param path
   * @throws BindingDeserializationException
   */
  public static void checkFile( String path ) throws BindingDeserializationException {
    var f = new File( path );

    var fullPath = f.getAbsolutePath();

    if( !f.isFile())
      throw new BindingDeserializationException( "File [{0}] does not exist", fullPath );

    try {
      DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse( fullPath );
    }
    catch( Exception e ){
      throw new BindingDeserializationException(
        "File [{0}] contains invalid xml. {1}", fullPath, e.getMessage() );
    }
  }
  //endregion

  //region Class 'Intersection' methods
  /**
   *
   * @param objects
   */
  public static void checkIntersections( Iterable<IPlcObject> objects ) throws BindingValidationException {
    checkBits( objects );
    checkWords( objects );
  }
  /**
   *
   * @param objects
   */
  private static void checkBits( Iterable<IPlcObject> objects ) throws BindingValidationException {
    var bits = UtilityHelper
      .toStream( objects )
      .filter( x -> x.type() == DataType.Bit )
      .map( x -> ( PlcBit )x )
      .toList();

    var set = new HashSet<PlcCoordinate>();

    for( var next : bits ){
      var coord = UtilityHelper.getCoordinate( next );

      if( set.contains( coord ) )
        throw new BindingValidationException(
          "Coordinate duplication at {0}", Stringer.toString( next, false ) );

      set.add( coord );
    }
  }
  /**
   *
   * @param objects
   * @throws BindingValidationException
   */
  private static void checkWords( Iterable<IPlcObject> objects ) throws BindingValidationException{
    var words = UtilityHelper
      .toStream( objects )
      .filter( x -> x.type() != DataType.Bit )
      .map( x -> ( IPlcWord )x )
      .toList();

    var checker = new PlcCoordinate.IntersectionChecker();

    for( var next : words ){
      var inter = checker.add( next );

      if( null != inter )
        throw new BindingValidationException(
          "Objects intersection: {0} and {1}",
          Stringer.toString( inter, false ),
          Stringer.toString( next, false ));
    }
  }
  //endregion
}
