package net.potatocloud.api.cluster;

import java.util.List;
import java.util.Optional;

public interface ClusterManager {

    ClusterNode localNode();

    List<ClusterNode> nodes();

    Optional<ClusterNode> find(String name);

}
