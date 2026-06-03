package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record NodeJoinPacket(String nodeName, String host, int port, long startedAt) implements Packet {

    public static final Codec<NodeJoinPacket> CODEC = new Codec<>() {

        @Override
        public void encode(NodeJoinPacket packet, PacketBuffer buf) {
            buf.writeString(packet.nodeName());
            buf.writeString(packet.host());
            buf.writeInt(packet.port());
            buf.writeLong(packet.startedAt());
        }

        @Override
        public NodeJoinPacket decode(PacketBuffer buf) {
            return new NodeJoinPacket(buf.readString(), buf.readString(), buf.readInt(), buf.readLong());
        }
    };
}
