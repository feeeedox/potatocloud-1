package net.potatocloud.core.networking.packet.packets.logging;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record LogMessagePacket(String level, String message) implements Packet {

    public static final Codec<LogMessagePacket> CODEC = new Codec<>() {

        @Override
        public void encode(LogMessagePacket packet, PacketBuffer buf) {
            buf.writeString(packet.level());
            buf.writeString(packet.message());
        }

        @Override
        public LogMessagePacket decode(PacketBuffer buf) {
            return new LogMessagePacket(buf.readString(), buf.readString());
        }
    };
}

