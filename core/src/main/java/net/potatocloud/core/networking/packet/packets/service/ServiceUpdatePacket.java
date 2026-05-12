package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

import java.util.Map;

public record ServiceUpdatePacket(
        String serviceName,
        String status,
        int maxPlayers,
        Map<String, Property<?>> propertyMap
) implements Packet {

    public static final Codec<ServiceUpdatePacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceUpdatePacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
            buf.writeString(packet.status());
            buf.writeInt(packet.maxPlayers());
            buf.writePropertyMap(packet.propertyMap());
        }

        @Override
        public ServiceUpdatePacket decode(PacketBuffer buf) {
            return new ServiceUpdatePacket(
                    buf.readString(),
                    buf.readString(),
                    buf.readInt(),
                    buf.readPropertyMap()
            );
        }
    };
}