package net.potatocloud.core.networking.packet.packets.group;

import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

import java.util.List;
import java.util.Map;

public record GroupUpdatePacket(
        String groupName,
        List<String> customJvmFlags,
        int maxPlayers,
        int maxMemory,
        int minOnlineCount,
        int maxOnlineCount,
        boolean fallback,
        int startPriority,
        int startPercentage,
        List<String> serviceTemplates,
        Map<String, Property<?>> propertyMap
) implements Packet {

    public static final Codec<GroupUpdatePacket> CODEC = new Codec<>() {

        @Override
        public void encode(GroupUpdatePacket packet, PacketBuffer buf) {
            buf.writeString(packet.groupName());
            buf.writeStringList(packet.customJvmFlags());
            buf.writeInt(packet.minOnlineCount());
            buf.writeInt(packet.maxOnlineCount());
            buf.writeInt(packet.maxPlayers());
            buf.writeInt(packet.maxMemory());
            buf.writeBoolean(packet.fallback());
            buf.writeInt(packet.startPriority());
            buf.writeInt(packet.startPercentage());
            buf.writeStringList(packet.serviceTemplates());
            buf.writePropertyMap(packet.propertyMap());
        }

        @Override
        public GroupUpdatePacket decode(PacketBuffer buf) {
            return new GroupUpdatePacket(
                    buf.readString(),
                    buf.readStringList(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readStringList(),
                    buf.readPropertyMap()
            );
        }
    };
}
