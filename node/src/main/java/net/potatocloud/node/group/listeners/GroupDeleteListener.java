package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;

@RequiredArgsConstructor
public class GroupDeleteListener implements PacketListener<GroupDeletePacket> {

    private final ServiceGroupManager groupManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<GroupDeletePacket> ctx) {
        final GroupDeletePacket packet = ctx.packet();
        final ServiceGroup group = groupManager.getServiceGroup(packet.groupName());
        if (group == null) {
            return;
        }

        groupManager.deleteServiceGroup(group);

        server.generateBroadcast().exclude(ctx.connection()).broadcast(packet);
    }
}
