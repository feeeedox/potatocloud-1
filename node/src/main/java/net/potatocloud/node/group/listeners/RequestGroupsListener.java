package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.group.GroupAddPacket;
import net.potatocloud.core.networking.packet.packets.group.RequestGroupsPacket;

@RequiredArgsConstructor
public class RequestGroupsListener implements PacketListener<RequestGroupsPacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void handle(PacketContext<RequestGroupsPacket> ctx) {
        for (ServiceGroup group : groupManager.getAllServiceGroups()) {
            ctx.connection().send(new GroupAddPacket(
                    group.getName(),
                    group.getPlatformName(),
                    group.getPlatformVersionName(),
                    group.getJavaCommand(),
                    group.getCustomJvmFlags(),
                    group.getMaxPlayers(),
                    group.getMaxMemory(),
                    group.getMinOnlineCount(),
                    group.getMaxOnlineCount(),
                    group.isStatic(),
                    group.isFallback(),
                    group.getStartPriority(),
                    group.getStartPercentage(),
                    group.getServiceTemplates(),
                    group.getPropertyMap()
            ));
        }
    }
}
