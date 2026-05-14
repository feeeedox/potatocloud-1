package net.potatocloud.network;

import net.potatocloud.common.Closeable;
import net.potatocloud.network.packet.Packet;

import java.util.UUID;

public interface NetworkConnection extends Closeable {

    UUID getId();

    void send(Packet packet);

}
