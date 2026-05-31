package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.cluster.NodeStatus;
import net.potatocloud.network.NetworkConnection;

import java.util.UUID;

public class ClusterNodeImpl implements ClusterNode {

    private final UUID id;
    private final String name;
    private final String host;
    private final int port;

    private NodeStatus status;
    private NetworkConnection connection;

    public ClusterNodeImpl(UUID id, String name, String host, int port, NodeStatus status, NetworkConnection connection) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.status = status;
        this.connection = connection;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public NodeStatus status() {
        return status;
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
}
