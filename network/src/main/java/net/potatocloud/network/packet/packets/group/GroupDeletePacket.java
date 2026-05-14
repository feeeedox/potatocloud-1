package net.potatocloud.network.packet.packets.group;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

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