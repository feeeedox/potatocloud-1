package net.potatocloud.network.packet.packets.group;

import net.potatocloud.api.group.Group;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record GroupAddPacket(Group group) implements Packet {

    public static final Codec<GroupAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(GroupAddPacket packet, PacketBuffer buf) {
            buf.writeGroup(packet.group());
        }

        @Override
        public GroupAddPacket decode(PacketBuffer buf) {
            return new GroupAddPacket(buf.readGroup());
        }
    };
}
