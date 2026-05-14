package net.potatocloud.node.platform.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.node.platform.PlatformManagerImpl;

@RequiredArgsConstructor
public class PlatformAddListener implements PacketListener<PlatformAddPacket> {

    private final PlatformManagerImpl platformManager;

    @Override
    public void handle(PacketContext<PlatformAddPacket> ctx) {
        platformManager.addPlatform(ctx.packet().platform());
    }
}
