package net.potatocloud.core.networking.packet.packets.property;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record PropertyRemovePacket(String propertyName) implements Packet {

    public static final Codec<PropertyRemovePacket> CODEC = new Codec<>() {

        @Override
        public void encode(PropertyRemovePacket packet, PacketBuffer buf) {
            buf.writeString(packet.propertyName());
        }

        @Override
        public PropertyRemovePacket decode(PacketBuffer buf) {
            return new PropertyRemovePacket(buf.readString());
        }
    };
}