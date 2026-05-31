package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record NodeJoinPacket(String nodeId, String name, String host, int port) implements Packet {

    public static final Codec<NodeJoinPacket> CODEC = new Codec<>() {

        @Override
        public void encode(NodeJoinPacket packet, PacketBuffer buf) {
            buf.writeString(packet.nodeId());
            buf.writeString(packet.name());
            buf.writeString(packet.host());
            buf.writeInt(packet.port());
        }

        @Override
        public NodeJoinPacket decode(PacketBuffer buf) {
            return new NodeJoinPacket(buf.readString(), buf.readString(), buf.readString(), buf.readInt());
        }
    };
}
