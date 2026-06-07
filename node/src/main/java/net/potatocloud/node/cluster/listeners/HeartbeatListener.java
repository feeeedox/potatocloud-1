package net.potatocloud.node.cluster.listeners;

import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.HeartbeatPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.cluster.ClusterNodeImpl;

public class HeartbeatListener implements PacketListener<HeartbeatPacket> {

    private final ClusterManagerImpl clusterManager;

    public HeartbeatListener(ClusterManagerImpl clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void handle(PacketContext<HeartbeatPacket> ctx) {
        clusterManager.remoteNode(ctx.packet().nodeName()).ifPresent(ClusterNodeImpl::updateHeartbeat);
    }
}
