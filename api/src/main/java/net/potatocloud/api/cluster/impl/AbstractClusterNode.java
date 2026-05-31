package net.potatocloud.api.cluster.impl;

import net.potatocloud.api.cluster.ClusterNode;

import java.util.UUID;

public class AbstractClusterNode implements ClusterNode {

    private final UUID id;
    private final String name;
    private final String host;
    private final int port;
    private final long startedAt;

    public AbstractClusterNode(UUID id, String name, String host, int port, long startedAt) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.startedAt = startedAt;
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
    public long startedAt() {
        return startedAt;
    }
}
