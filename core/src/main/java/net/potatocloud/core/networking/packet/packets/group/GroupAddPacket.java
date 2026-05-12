package net.potatocloud.core.networking.packet.packets.group;

import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

import java.util.List;
import java.util.Map;

public record GroupAddPacket(
        String groupName,
        String platformName,
        String platformVersion,
        String javaCommand,
        List<String> customJvmFlags,
        int maxPlayers,
        int maxMemory,
        int minOnlineCount,
        int maxOnlineCount,
        boolean isStatic,
        boolean fallback,
        int startPriority,
        int startPercentage,
        List<String> serviceTemplates,
        Map<String, Property<?>> propertyMap
) implements Packet {

    public static final Codec<GroupAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(GroupAddPacket packet, PacketBuffer buf) {
            buf.writeString(packet.groupName());
            buf.writeString(packet.platformName());
            buf.writeString(packet.platformVersion());
            buf.writeString(packet.javaCommand());
            buf.writeStringList(packet.customJvmFlags());
            buf.writeInt(packet.maxPlayers());
            buf.writeInt(packet.maxMemory());
            buf.writeInt(packet.maxOnlineCount());
            buf.writeInt(packet.minOnlineCount());
            buf.writeBoolean(packet.isStatic());
            buf.writeBoolean(packet.fallback());
            buf.writeInt(packet.startPriority());
            buf.writeInt(packet.startPercentage());
            buf.writeStringList(packet.serviceTemplates());
            buf.writePropertyMap(packet.propertyMap);
        }

        @Override
        public GroupAddPacket decode(PacketBuffer buf) {
            return new GroupAddPacket(
                    buf.readString(),
                    buf.readString(),
                    buf.readString(),
                    buf.readString(),
                    buf.readStringList(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readStringList(),
                    buf.readPropertyMap()
            );
        }
    };
}