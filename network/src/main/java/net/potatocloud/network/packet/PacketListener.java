package net.potatocloud.network.packet;

public interface PacketListener<T extends Packet> {

    void handle(PacketContext<T> ctx);

}
