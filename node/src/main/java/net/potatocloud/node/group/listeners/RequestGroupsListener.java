package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.Group;
import net.potatocloud.api.group.GroupManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.network.packet.packets.group.RequestGroupsPacket;

@RequiredArgsConstructor
public class RequestGroupsListener implements PacketListener<RequestGroupsPacket> {

    private final GroupManager groupManager;

    @Override
    public void handle(PacketContext<RequestGroupsPacket> ctx) {
        for (Group group : groupManager.groups()) {
            ctx.connection().send(new GroupAddPacket(group));
        }
    }
}
