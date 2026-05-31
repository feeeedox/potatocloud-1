package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.UUID;

public record NodeLeavePacket(UUID nodeId) implements Packet {

    public static final Codec<NodeLeavePacket> CODEC = new Codec<>() {

        @Override
        public void encode(NodeLeavePacket packet, PacketBuffer buf) {
            buf.writeUUID(packet.nodeId());
        }

        @Override
        public NodeLeavePacket decode(PacketBuffer buf) {
            return new NodeLeavePacket(buf.readUUID());
        }
    };
}
