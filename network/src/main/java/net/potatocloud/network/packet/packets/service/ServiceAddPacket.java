package net.potatocloud.network.packet.packets.service;

import net.potatocloud.api.service.Service;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

public record ServiceAddPacket(Service service, String requestId) implements Packet {

    public static final Codec<ServiceAddPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceAddPacket packet, PacketBuffer buf) {
            buf.writeService(packet.service());
            buf.writeString(packet.requestId());
        }

        @Override
        public ServiceAddPacket decode(PacketBuffer buf) {
            return new ServiceAddPacket(buf.readService(), buf.readString());
        }
    };
}
