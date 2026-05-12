package net.potatocloud.core.networking.packet;

import net.potatocloud.core.networking.netty.PacketBuffer;

public interface Packet {

    interface Codec<T extends Packet> {
        void encode(T packet, PacketBuffer buf);

        T decode(PacketBuffer buf);
    }
}
