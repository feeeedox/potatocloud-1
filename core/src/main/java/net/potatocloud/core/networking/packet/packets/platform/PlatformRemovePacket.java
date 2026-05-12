package net.potatocloud.core.networking.packet.packets.platform;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record PlatformRemovePacket(String platformName) implements Packet {

    public static final Codec<PlatformRemovePacket> CODEC = new Codec<>() {

        @Override
        public void encode(PlatformRemovePacket packet, PacketBuffer buf) {
            buf.writeString(packet.platformName());
        }

        @Override
        public PlatformRemovePacket decode(PacketBuffer buf) {
            return new PlatformRemovePacket(buf.readString());
        }
    };
}