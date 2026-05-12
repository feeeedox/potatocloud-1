package net.potatocloud.core.networking.packet.packets.group;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record GroupDeletePacket(String groupName) implements Packet {

    public static final Codec<GroupDeletePacket> CODEC = new Codec<>() {

        @Override
        public void encode(GroupDeletePacket packet, PacketBuffer buf) {
            buf.writeString(packet.groupName());
        }

        @Override
        public GroupDeletePacket decode(PacketBuffer buf) {
            return new GroupDeletePacket(buf.readString());
        }
    };
}