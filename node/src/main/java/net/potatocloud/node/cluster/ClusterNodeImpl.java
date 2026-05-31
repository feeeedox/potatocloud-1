package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.impl.AbstractClusterNode;
import net.potatocloud.network.NetworkConnection;

import java.util.UUID;

public class ClusterNodeImpl extends AbstractClusterNode {

    private final NetworkConnection connection;
    private volatile long lastHeartbeat = System.currentTimeMillis();

    public ClusterNodeImpl(UUID id, String name, String host, int port, long startedAt, NetworkConnection connection) {
        super(id, name, host, port, startedAt);
        this.connection = connection;
    }

    public NetworkConnection connection() {
        return connection;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isTimedOut(long timeoutMs) {
        return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
    }
}
