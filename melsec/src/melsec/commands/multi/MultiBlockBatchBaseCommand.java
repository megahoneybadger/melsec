package melsec.commands.multi;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcString;
import melsec.bindings.PlcStruct;
import melsec.commands.ICommand;
import melsec.io.*;
import melsec.commands.CommandCode;
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
  protected static final int MAX_POINTS = 960;
  /**
   *
   */
  protected static final int MAX_BLOCKS = 120;
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
      case Bit, U2, I2 -> 1;
      case U4, I4 -> 2;
      case String -> {
        var s = ( PlcString ) o;
        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;
        var points = s.size() / 2;
        yield ( points + extra );
      }
      case Struct -> {
        var st = ( PlcStruct )o;
        var items = st.items();

        if( 0 == items.size() )
          yield 0;

        var last = items.get( items.size() - 1 );
        var words = last.address() - st.address() + getPointsCount( last );

        yield words;
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
    map = new HashMap<>();
    results = new ArrayList<>();
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

    UtilityHelper.toList( u.items() )
      .stream()
      .forEach( x -> map.put( UtilityHelper.getPlcObjectKey( x.object()), x ) );
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
      var key = UtilityHelper.getPlcObjectKey( o );

      if( !map.containsKey( key ) )
        continue;

      var req = map.get( key );
      output.put( key, req.toResponse( o ) );
    }

    var res = new ArrayList<IOResponseItem>();

    for( var item: unit.items() ){
      var key = UtilityHelper.getPlcObjectKey( item.object());

      if( !output.containsKey( key ) )
        continue;

      res.add( output.get( key ) );
    }

    return new IOResponse( res );
  }
  //endregion
}
