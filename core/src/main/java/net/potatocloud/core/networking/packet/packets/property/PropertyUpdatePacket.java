package net.potatocloud.core.networking.packet.packets.property;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record PropertyUpdatePacket(String propertyName, Object value) implements Packet {

    public static final Codec<PropertyUpdatePacket> CODEC = new Codec<>() {

        @Override
        public void encode(PropertyUpdatePacket packet, PacketBuffer buf) {
            buf.writeString(packet.propertyName());
            buf.writeObject(packet.value());
        }

        @Override
        public PropertyUpdatePacket decode(PacketBuffer buf) {
            return new PropertyUpdatePacket(buf.readString(), buf.readObject());
        }
    };
}