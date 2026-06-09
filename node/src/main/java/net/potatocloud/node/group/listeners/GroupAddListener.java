package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.group.ServiceGroupManagerImpl;

@RequiredArgsConstructor
public class GroupAddListener implements PacketListener<GroupAddPacket> {

    private final ServiceGroupManagerImpl groupManager;
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<GroupAddPacket> ctx) {
        final GroupAddPacket packet = ctx.packet();

        if (groupManager.existsServiceGroup(packet.group().name())) {
            return;
        }

        if (ctx.connection().type() == ConnectionType.NODE) {
            groupManager.registerServiceGroup(packet.group());
            server.broadcast().connectors().send(packet);
        } else {
            groupManager.addServiceGroup(packet.group());
            server.broadcast().connectors().exclude(ctx.connection()).send(packet);
            clusterManager.broadcast(packet);
        }
    }
}
