package melsec.events.commands;

import melsec.commands.ICommand;
import melsec.events.IEventArgs;

public record CommandEventArgs( ICommand command ) implements IEventArgs {

  public CommandEventArgs {
    command = command.copy();
  }

  @Override
  public String toString(){
    return command.toString();
  }
}
