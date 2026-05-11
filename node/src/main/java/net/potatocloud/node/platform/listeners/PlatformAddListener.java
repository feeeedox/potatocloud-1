package net.potatocloud.node.platform.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.node.platform.PlatformManagerImpl;

@RequiredArgsConstructor
public class PlatformAddListener implements PacketListener<PlatformAddPacket> {

    private final PlatformManagerImpl platformManager;

    @Override
    public void handle(PacketContext<PlatformAddPacket> ctx) {
        platformManager.addPlatform(ctx.packet().getPlatform());
    }
}
