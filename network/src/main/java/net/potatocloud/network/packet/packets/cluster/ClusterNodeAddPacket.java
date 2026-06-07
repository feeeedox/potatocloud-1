package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record ClusterNodeAddPacket(ClusterNode node) implements Packet {

    public static final Codec<ClusterNodeAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ClusterNodeAddPacket packet, PacketBuffer buf) {
            buf.writeClusterNode(packet.node());
        }

        @Override
        public ClusterNodeAddPacket decode(PacketBuffer buf) {
            return new ClusterNodeAddPacket(buf.readClusterNode());
        }
    };
}
