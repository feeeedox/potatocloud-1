package net.potatocloud.core.networking.packet.packets.player;

import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

import java.util.Map;
import java.util.UUID;

public record CloudPlayerAddPacket(
        String username,
        UUID uniqueId,
        String connectedProxyName,
        String connectedServiceName,
        Map<String, Property<?>> propertyMap
) implements Packet {

    public static final Codec<CloudPlayerAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(CloudPlayerAddPacket packet, PacketBuffer buf) {
            buf.writeString(packet.username());
            buf.writeString(packet.uniqueId().toString());
            buf.writeString(packet.connectedProxyName());
            buf.writeString(packet.connectedServiceName());
            buf.writePropertyMap(packet.propertyMap());
        }

        @Override
        public CloudPlayerAddPacket decode(PacketBuffer buf) {
            return new CloudPlayerAddPacket(
                    buf.readString(),
                    UUID.fromString(buf.readString()),
                    buf.readString(),
                    buf.readString(),
                    buf.readPropertyMap()
            );
        }
    };
}