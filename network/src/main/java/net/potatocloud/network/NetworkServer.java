package net.potatocloud.network;

import net.potatocloud.network.packet.Packet;

import java.util.Collection;
import java.util.function.Consumer;

public interface NetworkServer extends NetworkComponent {

    void start(String hostname, int port);

    boolean running();

    Collection<NetworkConnection> connectedSessions();

    int port();

    void send(NetworkConnection client, Packet packet);

    default void addDisconnectListener(Consumer<NetworkConnection> listener) {}

    default Broadcast broadcast() {
        return new Broadcast(this);
    }
}
