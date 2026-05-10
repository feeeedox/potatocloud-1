package net.potatocloud.core.networking;

import net.potatocloud.common.Closeable;
import net.potatocloud.core.networking.packet.Packet;
import net.potatocloud.core.networking.packet.PacketListener;

public interface NetworkComponent extends Closeable {

    <T extends Packet> void on(Class<T> packetClass, PacketListener<T> listener);

}
