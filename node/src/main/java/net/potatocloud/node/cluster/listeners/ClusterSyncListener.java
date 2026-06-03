package net.potatocloud.node.cluster.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.ClusterSyncPacket;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.player.CloudPlayerManagerImpl;
import net.potatocloud.node.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class ClusterSyncListener implements PacketListener<ClusterSyncPacket> {

    private final ServiceGroupManagerImpl groupManager;
    private final ServiceManagerImpl serviceManager;
    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void handle(PacketContext<ClusterSyncPacket> ctx) {
        final ClusterSyncPacket packet = ctx.packet();

        for (ServiceGroup group : packet.groups()) {
            groupManager.registerServiceGroup(group);
        }

        for (Service service : packet.services()) {
            if (serviceManager.getService(service.getName()) == null) {
                serviceManager.addService(service);
            }
        }

        for (CloudPlayer player : packet.players()) {
            if (playerManager.getCloudPlayer(player.getUniqueId()) == null) {
                playerManager.registerPlayer(player);
            }
        }
    }
}
