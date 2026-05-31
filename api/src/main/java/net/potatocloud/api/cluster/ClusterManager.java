package net.potatocloud.api.cluster;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClusterManager {

    ClusterNode localNode();

    Collection<ClusterNode> nodes();

    Optional<ClusterNode> get(UUID nodeId);

}
