package net.potatocloud.core.networking.packet.packets.player;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record CloudPlayerConnectPacket(String playerUsername, String serviceName) implements Packet {

    public static final Codec<CloudPlayerConnectPacket> CODEC = new Codec<>() {

        @Override
        public void encode(CloudPlayerConnectPacket packet, PacketBuffer buf) {
            buf.writeString(packet.playerUsername());
            buf.writeString(packet.serviceName());
        }

        @Override
        public CloudPlayerConnectPacket decode(PacketBuffer buf) {
            return new CloudPlayerConnectPacket(buf.readString(), buf.readString());
        }
    };
}