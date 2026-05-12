package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record StartServicePacket(String groupName, String requestId) implements Packet {

    public static final Codec<StartServicePacket> CODEC = new Codec<>() {

        @Override
        public void encode(StartServicePacket packet, PacketBuffer buf) {
            buf.writeString(packet.groupName());
            buf.writeString(packet.requestId());
        }

        @Override
        public StartServicePacket decode(PacketBuffer buf) {
            return new StartServicePacket(buf.readString(), buf.readString());
        }
    };
}