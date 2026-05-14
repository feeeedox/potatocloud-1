package net.potatocloud.network.packet.packets.property;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

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