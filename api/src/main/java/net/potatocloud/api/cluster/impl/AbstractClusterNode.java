package net.potatocloud.api.cluster.impl;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.cluster.NodeStatus;

import java.util.UUID;

public class AbstractClusterNode implements ClusterNode {

    private final UUID id;
    private final String name;
    private final String host;
    private final int port;
    protected volatile NodeStatus status;

    public AbstractClusterNode(UUID id, String name, String host, int port, NodeStatus status) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.status = status;
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
}
