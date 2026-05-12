package net.potatocloud.core.networking.packet.packets.property;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record RequestPropertiesPacket() implements Packet {

    public static final Codec<RequestPropertiesPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestPropertiesPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestPropertiesPacket decode(PacketBuffer buf) {
            return new RequestPropertiesPacket();
        }
    };
}