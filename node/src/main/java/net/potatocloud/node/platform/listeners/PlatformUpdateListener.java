package net.potatocloud.node.platform.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.platform.PlatformManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.platform.PlatformUpdatePacket;

@RequiredArgsConstructor
public class PlatformUpdateListener implements PacketListener<PlatformUpdatePacket> {

    private final PlatformManager platformManager;

    @Override
    public void handle(PacketContext<PlatformUpdatePacket> ctx) {
        platformManager.find(ctx.packet().platform().name())
                .ifPresent(platform -> platform.versions(ctx.packet().platform().versions()));
    }
}
