package net.potatocloud.core.networking.packet.request;

import net.potatocloud.core.networking.packet.Packet;

public abstract class ResponsePacket implements Packet {

    private int requestId;

    public int requestId() {
        return requestId;
    }

    public void requestId(int requestId) {
        this.requestId = requestId;
    }
}