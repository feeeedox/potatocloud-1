package net.potatocloud.connector.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.common.PropertyUtil;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupUpdatePacket;

@RequiredArgsConstructor
public class GroupUpdateListener implements PacketListener<GroupUpdatePacket> {

    private final ServiceGroupManager groupManager;

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
        packet.customJvmFlags().forEach(group::addCustomJvmFlag);

        group.getPropertyMap().clear();
        for (Property<?> property : packet.propertyMap().values()) {
            PropertyUtil.setPropertyUnchecked(group, property);
        }
    }
}
