package net.potatocloud.network.packet.packets.property;

import net.potatocloud.api.property.Property;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record PropertyAddPacket(Property<?> property) implements Packet {

    public static final Codec<PropertyAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(PropertyAddPacket packet, PacketBuffer buf) {
            buf.writeProperty(packet.property());
        }

        @Override
        public PropertyAddPacket decode(PacketBuffer buf) {
            return new PropertyAddPacket(buf.readProperty());
        }
    };
}