package melsec.simulation.net;

import melsec.simulation.Memory;

import java.nio.channels.AsynchronousSocketChannel;

public record ChannelParams( Memory memory,
                             AsynchronousSocketChannel socket,
                             IClientDisconnectedEvent handler ) {
 }

