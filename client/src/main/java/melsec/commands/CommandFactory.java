package melsec.commands;

import melsec.bindings.IPlcObject;
import melsec.commands.batch.BatchReadCommand;
import melsec.commands.multi.MultiBlockBatchReadCommand;
import melsec.commands.multi.MultiBlockBatchWriteCommand;
import melsec.commands.random.RandomWriteCommand;
import melsec.types.CommandCode;
import melsec.types.io.IORequest;
import melsec.types.io.IORequestItem;
import melsec.types.io.IORequestUnit;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {

  //region Class members
  /**
   *
   */
  private IORequest request;
  //endregion

  //region Class 'Unit stage' methods
  /**
   *
   * @return
   */
  public Iterable<ICommand> toCommands( IORequest r ){
    request = r;

    var units = splitReadWrite();

    var res = new ArrayList<ICommand>();

    for( var u : units ){
      var commands = switch( u.operation() ){
        case Read -> toReadCommands( u );
        case Write -> toWriteCommands( u );
      };

      if( null != commands && commands.size() > 0 ){
        res.addAll( commands );
      }
    }

    return res;
  }
  /**
   * Splits request into contiguous read and write units.
   * @return
   */
  private Iterable<IORequestUnit> splitReadWrite(){
    var res = new ArrayList<IORequestUnit>();

    IORequestItem prev = null;
    var items = new ArrayList<IORequestItem>();

    var source = request.items();
    var completeHandler = request.completeHandler();


    for( var item: source ){
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
  //endregion

  //region Class 'Read Commands' methods
  /**
   *
   * @param unit
   * @return
   */
  private List<ICommand> toReadCommands( IORequestUnit unit ){
    var res = new ArrayList<ICommand>();

    CommandCode prev = null;
    var items = new ArrayList<IORequestItem>();

    for( var item: unit.items() ){
      var cur = getReadCommandCode( item.object() );

      if( prev != null && prev != cur ){
        res.addAll( toReadCommands( unit.with( items ), prev ) );
        items.clear();
      }

      items.add( item );

      prev = cur;
    }

    if( items.size() > 0 ){
      res.addAll( toReadCommands( unit.with( items ), prev ) );
    }

    return res;
  }
  /**
   *
   * @param unit
   * @param code
   * @return
   */
  private List<ICommand> toReadCommands( IORequestUnit unit, CommandCode code ){
    var res = new ArrayList<ICommand>();

    var commands = switch( code ){
      case BatchRead -> BatchReadCommand.split( unit );
      case MultiBlockBatchRead -> MultiBlockBatchReadCommand.split( unit );
      default -> null;
    };

    if( null != commands && commands.size() > 0 ){
      res.addAll( commands );
    }

    return res;
  }
  /**
   *
   * @param o
   * @return
   */
  private CommandCode getReadCommandCode( IPlcObject o ){
    return switch( o.type() ){
      case Binary -> CommandCode.BatchRead;
      default -> CommandCode.MultiBlockBatchRead;
    };
  }

  //endregion

  //region Class 'Write Commands' methods
  /**
   * @param unit
   * @return
   */
  private List<ICommand> toWriteCommands( IORequestUnit unit ) {
    var res = new ArrayList<ICommand>();

    CommandCode prev = null;
    var items = new ArrayList<IORequestItem>();

    for( var item: unit.items() ){
      var cur = getWriteCommandCode( item.object() );

      if( prev != null && prev != cur ){
        res.addAll( toWriteCommands( unit.with( items ), prev ) );
        items.clear();
      }

      items.add( item );

      prev = cur;
    }

    if( items.size() > 0 ){
      res.addAll( toWriteCommands( unit.with( items ), prev ) );
    }

    return res;
  }
  /**
   *
   * @param unit
   * @param code
   * @return
   */
  private List<ICommand> toWriteCommands( IORequestUnit unit, CommandCode code ){
    var res = new ArrayList<ICommand>();

    var commands = switch( code ){
      //case BatchWrite -> BatchReadCommand.split( unit );todo
      case MultiBlockBatchWrite -> MultiBlockBatchWriteCommand.split( unit );
      case RandomWrite -> RandomWriteCommand.split( unit );
      default -> null;
    };

    if( null != commands && commands.size() > 0 ){
      res.addAll( commands );
    }

    return res;
  }
  /**
   *
   * @param o
   * @return
   */
  private CommandCode getWriteCommandCode( IPlcObject o ){
    return switch( o.type() ){
      case Binary -> CommandCode.BatchWrite;
      case Bit -> CommandCode.RandomWrite;
      default -> CommandCode.MultiBlockBatchWrite;
    };
  }
  //endregion
}
