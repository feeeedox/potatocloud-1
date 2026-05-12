package net.potatocloud.core.networking.packet.packets.platform;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record PlatformAddPacket(Platform platform) implements Packet {

    public static final Codec<PlatformAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(PlatformAddPacket packet, PacketBuffer buf) {
            buf.writePlatform(packet.platform());
        }

        @Override
        public PlatformAddPacket decode(PacketBuffer buf) {
            return new PlatformAddPacket(buf.readPlatform());
        }
    };
}