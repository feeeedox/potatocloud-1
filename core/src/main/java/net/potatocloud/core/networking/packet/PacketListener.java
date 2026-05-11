package net.potatocloud.core.networking.packet;

public interface PacketListener<T extends Packet> {

    void handle(PacketContext<T> ctx);

}
