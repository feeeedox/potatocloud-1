package net.potatocloud.node.platform.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.network.packet.packets.platform.RequestPlatformsPacket;

@RequiredArgsConstructor
public class RequestPlatformsListener implements PacketListener<RequestPlatformsPacket> {

    private final PlatformManager platformManager;

    @Override
    public void handle(PacketContext<RequestPlatformsPacket> ctx) {
        for (Platform platform : platformManager.getPlatforms()) {
            ctx.connection().send(new PlatformAddPacket(platform));
        }
    }
}
