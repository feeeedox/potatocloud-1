package net.potatocloud.core.networking.packet.packets.group;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

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

