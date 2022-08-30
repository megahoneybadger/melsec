package melsec.io;

import melsec.bindings.*;
import melsec.io.commands.ICommand;
import melsec.io.commands.MultiBlockBatchReadCommand;
import melsec.io.commands.MultiBlockBatchWriteCommand;

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
  private IOCompleteEventHandler eventHandler;
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
   *
   * @return
   */
  public List<ICommand> toCommands(){
    var res = new ArrayList<ICommand>();
    IORequestItem prev = null;

    var group = new ArrayList<IORequestItem>();

    for( var item: list ){
      if( prev == null || prev.type() == item.type() ){
        group.add( item );
      } else{
        var plcObjects = group
          .stream()
          .map( x -> x.object() )
          .toList();

        var commands = switch( prev.type() ){
          case Read -> MultiBlockBatchReadCommand.split( plcObjects, eventHandler );

          case Write -> MultiBlockBatchWriteCommand.split( plcObjects, eventHandler );
        };

        res.addAll( commands );
        group.clear();
      }

      prev = item;
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

    public Builder read( IPlcObject o ){
      list.add( new IORequestItem( IOType.Read, o ) );

      return this;
    }

    public Builder write( IPlcObject o ){
      list.add( new IORequestItem( IOType.Write, o ) );

      return this;
    }

    public Builder complete( IOCompleteEventHandler e){
      eventHandler = e;

      return this;
    }



    public IORequest build(){
      var res = new IORequest();
      res.list = new ArrayList<IORequestItem>( list );
      res.eventHandler = eventHandler;

      return res;
    }

  }
  //endregion
}




