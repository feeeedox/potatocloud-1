package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packet.Packet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Broadcast {

    private final NetworkServer server;

    public Broadcast(NetworkServer server) {
        this.server = server;
    }

    private final Set<NetworkConnection> excludeConnections = new HashSet<>();
    private Predicate<NetworkConnection> filter = null;

    public Broadcast exclude(NetworkConnection connection) {
        excludeConnections.add(connection);
        return this;
    }

    public Broadcast filter(Predicate<NetworkConnection> predicate) {
        this.filter = predicate;
        return this;
    }

    public void broadcast(Packet packet) {
        server.connectedSessions().forEach(connection -> {
            if (excludeConnections.contains(connection)) {
                return;
            }

            if (filter != null && !filter.test(connection)) {
                return;
            }

            server.send(connection, packet);
        });
    }
}
