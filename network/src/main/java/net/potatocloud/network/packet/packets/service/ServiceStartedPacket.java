package net.potatocloud.network.packet.packets.service;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

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