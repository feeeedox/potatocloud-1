package net.potatocloud.api.cluster.impl;

import net.potatocloud.api.cluster.ClusterNode;

import java.time.Instant;

public class AbstractClusterNode implements ClusterNode {

    private final String name;
    private final String host;
    private final int port;
    private final Instant startedAt;

    public AbstractClusterNode(String name, String host, int port, Instant startedAt) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.startedAt = startedAt;
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
    public Instant startedAt() {
        return startedAt;
    }
}
