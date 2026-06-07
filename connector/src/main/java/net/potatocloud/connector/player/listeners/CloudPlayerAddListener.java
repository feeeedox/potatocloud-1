package net.potatocloud.connector.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.connector.player.CloudPlayerManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerAddPacket;

@RequiredArgsConstructor
public class CloudPlayerAddListener implements PacketListener<CloudPlayerAddPacket> {

    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void handle(PacketContext<CloudPlayerAddPacket> ctx) {
        playerManager.registerPlayerLocal(ctx.packet().player());
    }
}
