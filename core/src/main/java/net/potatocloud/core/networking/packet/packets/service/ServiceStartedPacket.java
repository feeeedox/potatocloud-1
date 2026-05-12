package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record ServiceStartedPacket(String serviceName) implements Packet {

    public static final Codec<ServiceStartedPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceStartedPacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
        }

        @Override
        public ServiceStartedPacket decode(PacketBuffer buf) {
            return new ServiceStartedPacket(buf.readString());
        }
    };
}