package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.impl.SimpleClusterNode;
import net.potatocloud.network.NetworkConnection;

import java.time.Instant;

public class ClusterNodeImpl extends SimpleClusterNode {

    private final NetworkConnection connection;
    private volatile long lastHeartbeat = System.currentTimeMillis();

    public ClusterNodeImpl(String name, String host, int port, Instant startedAt, NetworkConnection connection) {
        super(name, host, port, startedAt);
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
