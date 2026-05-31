package net.potatocloud.network.packet.packets.player;

import net.potatocloud.api.property.Property;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

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
            buf.writeUUID(packet.uniqueId());
            buf.writeString(packet.connectedProxyName());
            buf.writeString(packet.connectedServiceName());
            buf.writePropertyMap(packet.propertyMap());
        }

        @Override
        public CloudPlayerAddPacket decode(PacketBuffer buf) {
            return new CloudPlayerAddPacket(
                    buf.readString(),
                    buf.readUUID(),
                    buf.readString(),
                    buf.readString(),
                    buf.readPropertyMap()
            );
        }
    };
}