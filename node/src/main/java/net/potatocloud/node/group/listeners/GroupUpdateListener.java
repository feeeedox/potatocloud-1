package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.common.PropertyUtil;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.group.config.ServiceGroupStorage;

@RequiredArgsConstructor
public class GroupUpdateListener implements PacketListener<GroupUpdatePacket> {

    private final ServiceGroupManager groupManager;
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<GroupUpdatePacket> ctx) {
        final GroupUpdatePacket packet = ctx.packet();

        groupManager.find(packet.groupName()).ifPresent(group -> {
            group.minServices(packet.minOnlineCount());
            group.maxServices(packet.maxOnlineCount());
            group.maxPlayers(packet.maxPlayers());
            group.maxMemory(packet.maxMemory());
            group.fallback(packet.fallback());
            group.startPriority(packet.startPriority());
            group.startPercentage(packet.startPercentage());

            group.templates().clear();
            packet.templates().forEach(group::addTemplate);

            group.customJvmFlags().clear();
            packet.customJvmFlags().forEach(group::addCustomJvmFlag);

            group.getPropertyMap().clear();
            packet.propertyMap().values().forEach(property -> PropertyUtil.setPropertyUnchecked(group, property));

            if (ctx.connection().type() == ConnectionType.CONNECTOR && groupManager instanceof ServiceGroupManagerImpl groupManagerImpl) {
                ServiceGroupStorage.save(group, groupManagerImpl.getGroupsPath());
            }
        });

        if (ctx.connection().type() == ConnectionType.CONNECTOR) {
            clusterManager.broadcast(packet);
        }
        server.broadcast().connectors().exclude(ctx.connection()).send(packet);
    }
}
