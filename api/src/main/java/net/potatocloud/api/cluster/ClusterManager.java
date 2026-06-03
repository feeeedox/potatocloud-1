package net.potatocloud.api.cluster;

import java.util.Collection;
import java.util.Optional;

public interface ClusterManager {

    ClusterNode localNode();

    Collection<ClusterNode> nodes();

    Optional<ClusterNode> get(String name);

}
