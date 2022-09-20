package melsec.simulation.net;

import melsec.events.IEventArgs;
import melsec.net.Endpoint;

public record ChannelEventArgs( EquipmentChannel channel ) implements IEventArgs {


}
