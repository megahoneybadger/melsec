package melsec.net;

import java.net.InetAddress;

public record Endpoint( InetAddress address, int port ) {
}
