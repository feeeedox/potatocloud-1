package net.potatocloud.api.cluster;

import java.util.UUID;

public interface ClusterNode {

    UUID id();

    String name();

    String host();

    int port();

    long startedAt();

}
