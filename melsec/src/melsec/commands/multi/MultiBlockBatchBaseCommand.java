package melsec.commands.multi;

import melsec.bindings.*;
import melsec.commands.ICommand;
import melsec.io.*;
import melsec.commands.CommandCode;
import melsec.types.DataType;
import melsec.utils.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
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
      case Bit -> 1;
      default -> ByteConverter.getBytesCount( ( IPlcWord ) o ) / 2;
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
      .forEach( x -> map.put( UtilityHelper.getKey( x.object()), x ) );
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
      var key = UtilityHelper.getKey( o );

      if( !map.containsKey( key ) )
        continue;

      var req = map.get( key );
      output.put( key, req.toResponse( o ) );
    }

    var res = new ArrayList<IOResponseItem>();

    for( var item: unit.items() ){
      var key = UtilityHelper.getKey( item.object());

      if( !output.containsKey( key ) )
        continue;

      res.add( output.get( key ) );
    }

    return new IOResponse( res );
  }
  //endregion
}
