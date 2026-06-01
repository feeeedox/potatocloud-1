package net.potatocloud.node.cluster.listeners;

import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.NodeLeavePacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

public class NodeLeaveListener implements PacketListener<NodeLeavePacket> {

    private final ClusterManagerImpl clusterManager;
    private final Logger logger;

    public NodeLeaveListener(ClusterManagerImpl clusterManager, Logger logger) {
        this.clusterManager = clusterManager;
        this.logger = logger;
    }

    @Override
    public void handle(PacketContext<NodeLeavePacket> ctx) {
        clusterManager.remoteNode(ctx.packet().nodeId()).ifPresent(node -> {
            clusterManager.remove(node);
            logger.info("Cluster node &a" + node.name() + " &7left the cluster");
        });
    }
}
