package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record NodeLeavePacket(String nodeName) implements Packet {

    public static final Codec<NodeLeavePacket> CODEC = new Codec<>() {

        @Override
        public void encode(NodeLeavePacket packet, PacketBuffer buf) {
            buf.writeString(packet.nodeName());
        }

        @Override
        public NodeLeavePacket decode(PacketBuffer buf) {
            return new NodeLeavePacket(buf.readString());
        }
    };
}
