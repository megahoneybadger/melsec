package melsec.types.events.commands;

import melsec.commands.ICommand;
import melsec.types.events.IEventArgs;

public record CommandEventArgs( ICommand command ) implements IEventArgs {

  public CommandEventArgs {
    //command = command.copy();
  }

  @Override
  public String toString(){
    return command.toString();
  }
}
