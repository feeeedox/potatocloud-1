package net.potatocloud.node.cluster.listeners;

import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.ClusterNodesResponsePacket;
import net.potatocloud.network.packet.packets.cluster.RequestClusterNodesPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

import java.util.ArrayList;

public class RequestClusterNodesListener implements PacketListener<RequestClusterNodesPacket> {

    private final ClusterManagerImpl clusterManager;

    public RequestClusterNodesListener(ClusterManagerImpl clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void handle(PacketContext<RequestClusterNodesPacket> ctx) {
        ctx.connection().send(new ClusterNodesResponsePacket(
                clusterManager.localNode(),
                new ArrayList<>(clusterManager.remoteNodes())
        ));
    }
}
