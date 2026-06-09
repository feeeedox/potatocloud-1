package net.potatocloud.connector.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.connector.group.ServiceGroupManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;

@RequiredArgsConstructor
public class GroupDeleteListener implements PacketListener<GroupDeletePacket> {

    private final ServiceGroupManagerImpl groupManager;

    @Override
    public void handle(PacketContext<GroupDeletePacket> ctx) {
        groupManager.deleteLocal(ctx.packet().groupName());
    }
}
