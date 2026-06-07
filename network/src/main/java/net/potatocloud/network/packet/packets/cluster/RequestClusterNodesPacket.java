package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record RequestClusterNodesPacket() implements Packet {

    public static final Codec<RequestClusterNodesPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestClusterNodesPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestClusterNodesPacket decode(PacketBuffer buf) {
            return new RequestClusterNodesPacket();
        }
    };
}
