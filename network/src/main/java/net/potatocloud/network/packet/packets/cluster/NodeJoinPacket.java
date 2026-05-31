package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.UUID;

public record NodeJoinPacket(UUID nodeId, String name, String host, int port, long startedAt) implements Packet {

    public static final Codec<NodeJoinPacket> CODEC = new Codec<>() {

        @Override
        public void encode(NodeJoinPacket packet, PacketBuffer buf) {
            buf.writeUUID(packet.nodeId());
            buf.writeString(packet.name());
            buf.writeString(packet.host());
            buf.writeInt(packet.port());
            buf.writeLong(packet.startedAt());
        }

        @Override
        public NodeJoinPacket decode(PacketBuffer buf) {
            return new NodeJoinPacket(buf.readUUID(), buf.readString(), buf.readString(), buf.readInt(), buf.readLong());
        }
    };
}
