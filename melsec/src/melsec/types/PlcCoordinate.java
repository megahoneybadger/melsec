package melsec.types;

import melsec.bindings.IPlcWord;
import melsec.utils.ByteConverter;
import melsec.utils.UtilityHelper;

import java.util.HashMap;

public record PlcCoordinate( IDeviceCode device, int address ) {

  public PlcCoordinate shiftleft(){
    return new PlcCoordinate( device, address - 1 );
  }

  public PlcCoordinate shiftRight(){
    return new PlcCoordinate( device, address + 1 );
  }

  public static class IntersectionChecker {
    private HashMap<PlcCoordinate, IPlcWord> dict = new HashMap<>();

    public IPlcWord add( IPlcWord w ){
      var points = ByteConverter.getPointsCount( w );
      var coord = UtilityHelper.getCoordinate( w );

      for( int i = 0; i < points; ++i ){
        if( dict.containsKey( coord ) )
          return dict.get( coord );

        coord = coord.shiftRight();
      }

      coord = UtilityHelper.getCoordinate( w );

      for( int i = 0; i < points; ++i ){
        dict.put( coord, w );
        coord = coord.shiftRight();
      }

      return null;
    }
  }
}
