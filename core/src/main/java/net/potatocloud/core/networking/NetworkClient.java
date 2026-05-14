package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packet.Packet;
import net.potatocloud.core.networking.packet.request.RequestPacket;
import net.potatocloud.core.networking.packet.request.ResponsePacket;

import java.util.concurrent.CompletableFuture;

public interface NetworkClient extends NetworkComponent {

    void connect(String host, int port);

    void send(Packet packet);

    void close();

    void addConnectionListener(ConnectionListener listener);

    <T extends ResponsePacket> CompletableFuture<T> request(RequestPacket packet, Class<T> type);

}
