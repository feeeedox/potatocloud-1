package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record RequestServicesPacket() implements Packet {

    public static final Codec<RequestServicesPacket> CODEC = new Codec<>() {

        @Override
        public void encode(RequestServicesPacket packet, PacketBuffer buf) {
        }

        @Override
        public RequestServicesPacket decode(PacketBuffer buf) {
            return new RequestServicesPacket();
        }
    };
}