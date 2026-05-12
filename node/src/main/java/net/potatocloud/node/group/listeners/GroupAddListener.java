package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.group.GroupAddPacket;
import net.potatocloud.node.group.ServiceGroupManagerImpl;

@RequiredArgsConstructor
public class GroupAddListener implements PacketListener<GroupAddPacket> {

    private final ServiceGroupManagerImpl groupManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<GroupAddPacket> ctx) {
        final GroupAddPacket packet = ctx.packet();

        groupManager.addServiceGroup(new ServiceGroupImpl(
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
                packet.propertyMap()
        ));

        server.generateBroadcast().exclude(ctx.connection()).broadcast(packet);
    }
}
