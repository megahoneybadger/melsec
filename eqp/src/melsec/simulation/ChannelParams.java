package melsec.simulation;

import melsec.simulation.Memory;
import melsec.simulation.events.IClientDisconnectedEvent;

import java.nio.channels.AsynchronousSocketChannel;

public record ChannelParams( Memory memory,
                             AsynchronousSocketChannel socket,
                             IClientDisconnectedEvent handler ) {
 }

