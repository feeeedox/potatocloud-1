package net.potatocloud.core.networking.packet;

import net.potatocloud.core.networking.netty.PacketBuffer;

public interface Packet {

    void write(PacketBuffer buf);

    void read(PacketBuffer buf);

}
