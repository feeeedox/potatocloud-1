package net.potatocloud.network;

import net.potatocloud.network.packet.Packet;

import java.util.List;

public interface NetworkServer extends NetworkComponent {

    void start(String hostname, int port);

    boolean running();

    List<NetworkConnection> connectedSessions();

    int port();

    void send(NetworkConnection client, Packet packet);

    default Broadcast generateBroadcast() {
        return new Broadcast(this);
    }
}
