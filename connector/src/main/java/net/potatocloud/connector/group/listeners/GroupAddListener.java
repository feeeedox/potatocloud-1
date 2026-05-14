package net.potatocloud.connector.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.connector.group.ServiceGroupManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;

@RequiredArgsConstructor
public class GroupAddListener implements PacketListener<GroupAddPacket> {

    private final ServiceGroupManagerImpl groupManager;

    @Override
    public void handle(PacketContext<GroupAddPacket> ctx) {
        final GroupAddPacket packet = ctx.packet();
        if (groupManager.existsServiceGroup(packet.groupName())) {
            return;
        }

        final ServiceGroupImpl group = new ServiceGroupImpl(
                packet.groupName(),
                packet.platformName(),
                packet.platformVersion(),
                packet.javaCommand(),
                packet.customJvmFlags(),
                packet.maxPlayers(),
                packet.maxMemory(),
                packet.minOnlineCount(),
                packet.maxOnlineCount(),
                packet.isStatic(),
                packet.fallback(),
                packet.startPriority(),
                packet.startPercentage(),
                packet.serviceTemplates(),
                packet.propertyMap()
        );

        groupManager.addServiceGroup(group);
    }
}
