package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record ServiceExecuteCommandPacket(String serviceName, String command) implements Packet {

    public static final Codec<ServiceExecuteCommandPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceExecuteCommandPacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
            buf.writeString(packet.command());
        }

        @Override
        public ServiceExecuteCommandPacket decode(PacketBuffer buf) {
            return new ServiceExecuteCommandPacket(buf.readString(), buf.readString());
        }
    };
}