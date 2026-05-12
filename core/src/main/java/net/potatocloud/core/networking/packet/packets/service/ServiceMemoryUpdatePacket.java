package net.potatocloud.core.networking.packet.packets.service;

import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

public record ServiceMemoryUpdatePacket(String serviceName, int usedMemory) implements Packet {

    public static final Codec<ServiceMemoryUpdatePacket> CODEC = new Codec<>() {

        @Override
        public void encode(ServiceMemoryUpdatePacket packet, PacketBuffer buf) {
            buf.writeString(packet.serviceName());
            buf.writeInt(packet.usedMemory());
        }

        @Override
        public ServiceMemoryUpdatePacket decode(PacketBuffer buf) {
            return new ServiceMemoryUpdatePacket(buf.readString(), buf.readInt());
        }
    };
}