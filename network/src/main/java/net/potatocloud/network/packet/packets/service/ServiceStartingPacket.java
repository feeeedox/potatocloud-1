package net.potatocloud.network.packet.packets.service;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record ServiceStartingPacket(String serviceName) implements Packet {

    public static final Codec<ServiceStartingPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceStartingPacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
        }

        @Override
        public ServiceStartingPacket decode(PacketBuffer buf) {
            return new ServiceStartingPacket(buf.readString());
        }
    };
}
