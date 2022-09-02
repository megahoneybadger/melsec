package melsec.io.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcString;
import melsec.io.*;
import melsec.io.commands.CommandCode;
import melsec.io.commands.ICommand;
import melsec.types.DataType;
import melsec.utils.UtilityHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MultiBlockBatchBaseCommand extends ICommand {

  //region Class constants
  /**
   *
   */
  public static final int MAX_POINTS = 960;
  /**
   *
   */
  public static final int MAX_BLOCKS = 120;
  //endregion

  //region Class members
  /**
   *
   */
  protected HashMap<String, IORequestItem> map;
  /**
   *
   */
  protected List<IPlcObject> results;
  /**
   *
   */
  protected List<IPlcObject> bits;
  /**
   *
   */
  protected List<IPlcObject> words;
  //endregion

  //region Class properties
  /**
   *
   * @param o
   * @return
   */
  protected static int getPointsCount( IPlcObject o ){
    return switch( o.type() ){
      case Bit, U1, U2, I1, I2 -> 1;
      case U4, I4, F4 -> 2;
      case U8, I8, F8 -> 4;
      case String -> {
        var s = ( PlcString ) o;
        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;
        var points = s.size() / 2;
        yield ( points + extra );
      }
      default -> 0;
    };
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param u
   */
  public MultiBlockBatchBaseCommand( IORequestUnit u ){
    unit = u;

    bits = UtilityHelper.toList( u.items() )
      .stream()
      .filter( x -> x.object().type() == DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    words = UtilityHelper.toList( u.items() )
      .stream()
      .filter( x -> x.object().type() != DataType.Bit )
      .map( x -> x.object() )
      .collect( Collectors.toList() );

    map = new HashMap<>();
    results = new ArrayList<>();

    UtilityHelper.toList( u.items() )
      .stream()
      .forEach( x -> map.put( x.object().key(), x ) );
  }
  /**
   *
   * @return
   */
  @Override
  public String toString(){
    String w = words.size() > 0 ? MessageFormat.format( "{0}w", words.size() ) : "";
    String b = bits.size() > 0 ? MessageFormat.format( "{0}b", bits.size() ) : "";
    String sep = ( !w.isEmpty() && !b.isEmpty() ) ? "|" : "";

    var c = ( code() == CommandCode.MultiBlockBatchWrite ) ? "w" : "r";

    return MessageFormat.format( "mbb{4}#{3} [{0}{1}{2}]", w, sep, b, id, c );
  }

  //endregion

  //region Class 'Response' methods
  /**
   *
   * @return
   */
  public IOResponse toResponse(){
    var output = new HashMap<String, IOResponseItem>();

    for( var o : results ){
      var key = o.key();

      if( !map.containsKey( key ) )
        continue;

      var req = map.get( key );
      output.put( key, req.toResponse( o ) );
    }

    var res = new ArrayList<IOResponseItem>();

    for( var item: unit.items() ){
      var key = item.object().key();

      if( !output.containsKey( key ) )
        continue;

      res.add( output.get( key ) );
    }

    return new IOResponse( res );
  }
  //endregion
}
