package melsec.simulation.events;

import melsec.types.events.IEventArgs;
import melsec.simulation.EquipmentChannel;

public record ChannelEventArgs( EquipmentChannel channel ) implements IEventArgs {


}
