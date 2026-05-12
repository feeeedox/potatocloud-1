package net.potatocloud.core.networking.packet.packets.platform;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record RequestPlatformsPacket() implements Packet {

    public static final Codec<RequestPlatformsPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestPlatformsPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestPlatformsPacket decode(PacketBuffer buf) {
            return new RequestPlatformsPacket();
        }
    };
}