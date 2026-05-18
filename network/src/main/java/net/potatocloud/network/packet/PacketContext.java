package net.potatocloud.network.packet;

import net.potatocloud.network.NetworkConnection;
import net.potatocloud.network.packet.request.ResponsePacket;

public record PacketContext<T extends Packet>(NetworkConnection connection, PacketManager packetManager, T packet, int requestId) {

    public boolean hasRequest() {
        return requestId != 0;
    }

    public void reply(ResponsePacket response) {
        if (!hasRequest()) {
            throw new IllegalStateException("No request available for reply()");
        }
        packetManager.requestId(response, requestId);
        connection.send(response);
        packetManager.removeRequest(packet);
    }
}
