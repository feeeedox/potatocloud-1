package net.potatocloud.node.platform.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformManager;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.platform.PlatformUpdatePacket;

@RequiredArgsConstructor
public class PlatformUpdateListener implements PacketListener<PlatformUpdatePacket> {

    private final PlatformManager platformManager;

    @Override
    public void handle(PacketContext<PlatformUpdatePacket> ctx) {
        final Platform platform = platformManager.getPlatform(ctx.packet().platform().getName());
        if (platform == null) {
            return;
        }
        platform.setVersions(ctx.packet().platform().getVersions());
    }
}
