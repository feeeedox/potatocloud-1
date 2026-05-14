package net.potatocloud.network;

import net.potatocloud.common.Closeable;
import net.potatocloud.network.packet.Packet;
import net.potatocloud.network.packet.PacketListener;

public interface NetworkComponent extends Closeable {

    <T extends Packet> void on(Class<T> packetClass, PacketListener<T> context);

}
