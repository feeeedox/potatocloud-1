package net.potatocloud.core.networking.packet;

import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.packet.request.ResponsePacket;

public record PacketContext<T extends Packet>(NetworkConnection connection, T packet, int requestId) {

    public boolean hasRequest() {
        return requestId != 0;
    }

    public void reply(ResponsePacket response) {
        if (!hasRequest()) {
            throw new IllegalStateException("No request available for reply()");
        }
        response.requestId(requestId);
        connection.send(response);
    }
}
