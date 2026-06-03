package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.group.ServiceGroupManagerImpl;

@RequiredArgsConstructor
public class GroupDeleteListener implements PacketListener<GroupDeletePacket> {

    private final ServiceGroupManagerImpl groupManager;
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<GroupDeletePacket> ctx) {
        final GroupDeletePacket packet = ctx.packet();

        if (ctx.connection().type() == ConnectionType.NODE) {
            groupManager.unregisterServiceGroup(packet.groupName());
            server.broadcast().connectors().send(packet);
        } else {
            if (!groupManager.deleteServiceGroupLocal(packet.groupName())) {
                return;
            }
            server.broadcast().connectors().exclude(ctx.connection()).send(packet);
            clusterManager.broadcast(packet);
        }
    }
}
