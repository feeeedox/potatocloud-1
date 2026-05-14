package net.potatocloud.network.packet.packets.group;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record RequestGroupsPacket() implements Packet {

    public static final Codec<RequestGroupsPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestGroupsPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestGroupsPacket decode(PacketBuffer buf) {
            return new RequestGroupsPacket();
        }
    };
}

