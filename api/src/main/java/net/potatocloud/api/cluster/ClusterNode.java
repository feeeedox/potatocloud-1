package net.potatocloud.api.cluster;

import java.time.Instant;

public interface ClusterNode {

    String name();

    String host();

    int port();

    Instant startedAt();

}
