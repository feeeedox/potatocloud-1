package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record HeartbeatPacket(String nodeName) implements Packet {

    public static final Codec<HeartbeatPacket> CODEC = new Codec<>() {

        @Override
        public void encode(HeartbeatPacket packet, PacketBuffer buf) {
            buf.writeString(packet.nodeName());
        }

        @Override
        public HeartbeatPacket decode(PacketBuffer buf) {
            return new HeartbeatPacket(buf.readString());
        }
    };
}
