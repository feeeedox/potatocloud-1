package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.common.PropertyUtil;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.group.ServiceGroupStorage;

@RequiredArgsConstructor
public class GroupUpdateListener implements PacketListener<GroupUpdatePacket> {

    private final ServiceGroupManager groupManager;
    private final NetworkServer server;

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

        if (groupManager instanceof ServiceGroupManagerImpl groupManagerImpl) {
            ServiceGroupStorage.saveToFile(group, groupManagerImpl.getGroupsPath());
        }

        server.generateBroadcast().exclude(ctx.connection()).broadcast(packet);
    }
}
