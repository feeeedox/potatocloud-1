package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.NodeStatus;
import net.potatocloud.api.cluster.impl.AbstractClusterNode;
import net.potatocloud.network.NetworkConnection;

import java.util.UUID;

public class ClusterNodeImpl extends AbstractClusterNode {

    private volatile NetworkConnection connection;
    private volatile long lastHeartbeat = System.currentTimeMillis();

    public ClusterNodeImpl(UUID id, String name, String host, int port, NodeStatus status, NetworkConnection connection) {
        super(id, name, host, port, status);
        this.connection = connection;
    }

    public void status(NodeStatus status) {
        this.status = status;
    }

    public NetworkConnection connection() {
        return connection;
    }

    public void connection(NetworkConnection connection) {
        this.connection = connection;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isTimedOut(long timeoutMs) {
        return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
    }
}
