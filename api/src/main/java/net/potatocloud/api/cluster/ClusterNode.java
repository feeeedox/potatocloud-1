package net.potatocloud.api.cluster;

public interface ClusterNode {

    String name();

    String host();

    int port();

    long startedAt();

}
