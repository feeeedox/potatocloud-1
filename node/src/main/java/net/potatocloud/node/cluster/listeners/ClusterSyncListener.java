package net.potatocloud.node.cluster.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.ClusterSyncPacket;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.network.packet.packets.player.CloudPlayerAddPacket;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.player.CloudPlayerManagerImpl;
import net.potatocloud.node.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class ClusterSyncListener implements PacketListener<ClusterSyncPacket> {

    private final ServiceGroupManagerImpl groupManager;
    private final ServiceManagerImpl serviceManager;
    private final CloudPlayerManagerImpl playerManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<ClusterSyncPacket> ctx) {
        final ClusterSyncPacket packet = ctx.packet();

        for (ServiceGroup group : packet.groups()) {
            if (groupManager.existsServiceGroup(group.name())) {
                continue;
            }
            groupManager.registerServiceGroup(group);
            server.broadcast().connectors().send(new GroupAddPacket(group));
        }

        for (Service service : packet.services()) {
            if (serviceManager.find(service.name()).isPresent()) {
                continue;
            }
            serviceManager.addService(service);
            server.broadcast().connectors().send(new ServiceAddPacket(service, null));
        }

        for (CloudPlayer player : packet.players()) {
            if (playerManager.find(player.uniqueId()).isPresent()) {
                continue;
            }
            playerManager.registerPlayer(player);
            server.broadcast().connectors().send(new CloudPlayerAddPacket(player));
        }
    }
}
