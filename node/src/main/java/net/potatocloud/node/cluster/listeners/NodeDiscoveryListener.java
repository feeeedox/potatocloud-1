package net.potatocloud.node.cluster.listeners;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.NodeDiscoveryPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

public class NodeDiscoveryListener implements PacketListener<NodeDiscoveryPacket> {

    private final ClusterManagerImpl clusterManager;

    public NodeDiscoveryListener(ClusterManagerImpl clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void handle(PacketContext<NodeDiscoveryPacket> ctx) {
        for (ClusterNode node : ctx.packet().nodes()) {
            if (clusterManager.remoteNode(node.name()).isPresent()) {
                continue;
            }
            clusterManager.connect(node.host(), node.port());
        }
    }
}
