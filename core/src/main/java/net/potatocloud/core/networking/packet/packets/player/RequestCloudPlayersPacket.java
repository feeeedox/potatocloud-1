package net.potatocloud.core.networking.packet.packets.player;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record RequestCloudPlayersPacket() implements Packet {

    public static final Codec<RequestCloudPlayersPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestCloudPlayersPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestCloudPlayersPacket decode(PacketBuffer buf) {
            return new RequestCloudPlayersPacket();
        }
    };
}