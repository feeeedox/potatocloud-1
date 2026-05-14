package net.potatocloud.network.packet.packets.service;

import net.potatocloud.api.property.Property;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.Map;

public record ServiceAddPacket(
        String serviceName,
        int serviceId,
        int port,
        long startTimestamp,
        String groupName,
        Map<String, Property<?>> propertyMap,
        String status,
        int maxPlayers,
        String requestId
) implements Packet {

    public static final Codec<ServiceAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceAddPacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
            buf.writeInt(packet.serviceId());
            buf.writeInt(packet.port());
            buf.writeLong(packet.startTimestamp());
            buf.writeString(packet.groupName());
            buf.writePropertyMap(packet.propertyMap());
            buf.writeString(packet.status());
            buf.writeInt(packet.maxPlayers());
            buf.writeString(packet.requestId());
        }

        @Override
        public ServiceAddPacket decode(PacketBuffer buf) {
            return new ServiceAddPacket(
                    buf.readString(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readLong(),
                    buf.readString(),
                    buf.readPropertyMap(),
                    buf.readString(),
                    buf.readInt(),
                    buf.readString()
            );
        }
    };
}