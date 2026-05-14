package net.potatocloud.network.packet;

import net.potatocloud.network.netty.PacketBuffer;

public interface Packet {

    interface Codec<T extends Packet> {
        void encode(T packet, PacketBuffer buf);

        T decode(PacketBuffer buf);
    }
}
