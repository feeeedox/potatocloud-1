package net.potatocloud.connector.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.api.property.Property;
import net.potatocloud.common.PropertyUtil;
import net.potatocloud.connector.player.CloudPlayerManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerUpdatePacket;

@RequiredArgsConstructor
public class CloudPlayerUpdateListener implements PacketListener<CloudPlayerUpdatePacket> {

    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void handle(PacketContext<CloudPlayerUpdatePacket> ctx) {
        final CloudPlayerUpdatePacket packet = ctx.packet();
        final CloudPlayerImpl player = (CloudPlayerImpl) playerManager.getCloudPlayer(packet.playerUniqueId());
        if (player == null) {
            return;
        }

        player.setConnectedProxyName(packet.connectedProxyName());
        player.setConnectedServiceName(packet.connectedServiceName());

        player.getPropertyMap().clear();
        for (Property<?> property : packet.propertyMap().values()) {
            PropertyUtil.setPropertyUnchecked(player, property);
        }
    }
}

