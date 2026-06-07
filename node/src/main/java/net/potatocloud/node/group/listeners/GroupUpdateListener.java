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
        final ServiceGroup group = groupManager.getServiceGroup(packet.groupName());
        if (group == null) {
            return;
        }

        group.setMinOnlineCount(packet.minOnlineCount());
        group.setMaxOnlineCount(packet.maxOnlineCount());
        group.setMaxPlayers(packet.maxPlayers());
        group.setMaxMemory(packet.maxMemory());
        group.setFallback(packet.fallback());
        group.setStartPriority(packet.startPriority());
        group.setStartPercentage(packet.startPercentage());

        group.getServiceTemplates().clear();
        packet.serviceTemplates().forEach(group::addServiceTemplate);

        group.getCustomJvmFlags().clear();
        for (String flag : packet.customJvmFlags()) {
            group.addCustomJvmFlag(flag);
        }

        group.getPropertyMap().clear();
        for (Property<?> property : packet.propertyMap().values()) {
            PropertyUtil.setPropertyUnchecked(group, property);
        }

        if (ctx.connection().type() == ConnectionType.CONNECTOR) {
            if (groupManager instanceof ServiceGroupManagerImpl groupManagerImpl) {
                ServiceGroupStorage.save(group, groupManagerImpl.getGroupsPath());
            }
            clusterManager.broadcast(packet);
        }

        server.broadcast().connectors().exclude(ctx.connection()).send(packet);
    }
}
