package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.UUID;

public record HeartbeatPacket(UUID nodeId) implements Packet {

    public static final Codec<HeartbeatPacket> CODEC = new Codec<>() {

        @Override
        public void encode(HeartbeatPacket packet, PacketBuffer buf) {
            buf.writeUUID(packet.nodeId());
        }

        @Override
        public HeartbeatPacket decode(PacketBuffer buf) {
            return new HeartbeatPacket(buf.readUUID());
        }
    };
}
