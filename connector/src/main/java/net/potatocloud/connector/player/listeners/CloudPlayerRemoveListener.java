package net.potatocloud.connector.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.connector.player.CloudPlayerManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerRemovePacket;

@RequiredArgsConstructor
public class CloudPlayerRemoveListener implements PacketListener<CloudPlayerRemovePacket> {

    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void handle(PacketContext<CloudPlayerRemovePacket> ctx) {
        playerManager.find(ctx.packet().playerUniqueId()).ifPresent(playerManager::unregisterPlayerLocal);
    }
}
