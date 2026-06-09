package net.potatocloud.node.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerAddPacket;
import net.potatocloud.network.packet.packets.player.RequestCloudPlayersPacket;

@RequiredArgsConstructor
public class RequestCloudPlayersListener implements PacketListener<RequestCloudPlayersPacket> {

    private final CloudPlayerManager playerManager;

    @Override
    public void handle(PacketContext<RequestCloudPlayersPacket> ctx) {
        for (CloudPlayer player : playerManager.players()) {
            ctx.connection().send(new CloudPlayerAddPacket(player));
        }
    }
}
