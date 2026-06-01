package net.potatocloud.node.cluster.listeners;

import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.NetworkConnection;
import net.potatocloud.node.cluster.ClusterManagerImpl;

import java.util.function.Consumer;

public class NodeDisconnectListener implements Consumer<NetworkConnection> {

    private final ClusterManagerImpl clusterManager;
    private final Logger logger;

    public NodeDisconnectListener(ClusterManagerImpl clusterManager, Logger logger) {
        this.clusterManager = clusterManager;
        this.logger = logger;
    }

    @Override
    public void accept(NetworkConnection connection) {
        // only warn if no clean leave packet was received before disconnect
        clusterManager.remoteNode(connection).ifPresent(node -> {
            clusterManager.remove(node);
            logger.warn("Cluster node &a" + node.name() + " &7lost connection");
        });
    }
}
