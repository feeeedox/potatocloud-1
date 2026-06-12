package net.potatocloud.connector.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.connector.group.GroupManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;

@RequiredArgsConstructor
public class GroupAddListener implements PacketListener<GroupAddPacket> {

    private final GroupManagerImpl groupManager;

    @Override
    public void handle(PacketContext<GroupAddPacket> ctx) {
        final GroupAddPacket packet = ctx.packet();

        if (groupManager.exists(packet.group().name())) {
            return;
        }

        groupManager.addGroup(packet.group());
    }
}
