package net.potatocloud.network.packet.packets.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record GroupAddPacket(ServiceGroup group) implements Packet {

    public static final Codec<GroupAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(GroupAddPacket packet, PacketBuffer buf) {
            buf.writeServiceGroup(packet.group());
        }

        @Override
        public GroupAddPacket decode(PacketBuffer buf) {
            return new GroupAddPacket(buf.readServiceGroup());
        }
    };
}
