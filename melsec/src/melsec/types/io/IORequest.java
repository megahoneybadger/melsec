package melsec.types.io;

import melsec.commands.ICommand;
import melsec.bindings.IPlcObject;
import melsec.commands.multi.MultiBlockBatchReadCommand;
import melsec.commands.multi.MultiBlockBatchWriteCommand;
import melsec.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.List;

public class IORequest {

  //region Class members
  /**
   *
   */
  private List<IORequestItem> list = new ArrayList<>();
  /**
   *
   */
  private IOCompleteEventHandler completeHandler;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public int count(){
    return list.size();
  }
  /**
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }
  //endregion

  //region Class public methods

  /**
   * Splits request into contiguous read and write units.
   * @return
   */
  public Iterable<IORequestUnit> toUnits(){
    var res = new ArrayList<IORequestUnit>();

    IORequestItem prev = null;
    var items = new ArrayList<IORequestItem>();
    var sequenceNo = 0;

    for( var item: list ){
      if( prev != null && prev.type() != item.type() ){
        res.add( new IORequestUnit( prev.type(), new ArrayList<>( items ), completeHandler ) );

        items.clear();
      }

      items.add( item );

      prev = item;
    }

    if( items.size() > 0 ){
      res.add( new IORequestUnit( prev.type(), new ArrayList<>( items ), completeHandler ) );
    }

    return res;
  }
  /**
   * Splits units multi block batch commands.
   * @return
   */
  public Iterable<ICommand> toMultiBlockBatchCommands(){
    var units = toUnits();
    var res = new ArrayList<ICommand>();

    for( var u : units ){
      var commands = switch( u.operation() ){
        case Read -> MultiBlockBatchReadCommand.split( u );
        case Write -> MultiBlockBatchWriteCommand.split( u );
      };

      if( null != commands && commands.size() > 0 ){
        res.addAll( commands );
      }
    }

    return res;
  }
  //endregion

  //region Class internal structs
  /**
   *
   */
  public static class Builder{

    private List<IORequestItem> list = new ArrayList<>();
    private IOCompleteEventHandler eventHandler;

    public int count(){
      return list.size();
    }

    public Builder read( IPlcObject o ){
      return add( new IORequestItem( IOType.Read, o ) );
    }

    public Builder read( IPlcObject... arr ){
      for( var o : arr ){
        read( o );
      }

      return this;
    }

    public Builder read( List<IPlcObject> list ){
      list.forEach( x -> read( x ) );

      return this;
    }

    public Builder write( IPlcObject o ){
      return add( new IORequestItem( IOType.Write, o ) );
    }

    public Builder add( IORequestItem item ){
      list.add( item );

      return this;
    }

    public Builder add( Iterable<IORequestItem> items ){
      list.addAll( UtilityHelper.toList( items ) );

      return this;
    }

    public Builder complete( IOCompleteEventHandler e){
      eventHandler = e;

      return this;
    }



    public IORequest build(){
      var res = new IORequest();
      res.list = new ArrayList<IORequestItem>( list );
      res.completeHandler = eventHandler;

      return res;
    }

  }
  //endregion
}




