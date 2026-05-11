package net.potatocloud.node.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.player.CloudPlayerAddPacket;
import net.potatocloud.core.networking.packet.packets.player.RequestCloudPlayersPacket;

@RequiredArgsConstructor
public class RequestCloudPlayersListener implements PacketListener<RequestCloudPlayersPacket> {

    private final CloudPlayerManager playerManager;

    @Override
    public void handle(PacketContext<RequestCloudPlayersPacket> ctx) {
        for (CloudPlayer player : playerManager.getOnlinePlayers()) {
            ctx.connection().send(new CloudPlayerAddPacket(
                    player.getUsername(),
                    player.getUniqueId(),
                    player.getConnectedProxyName(),
                    player.getConnectedServiceName(),
                    player.getPropertyMap()
            ));
        }
    }
}
