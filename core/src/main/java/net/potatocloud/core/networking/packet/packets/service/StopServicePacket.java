package net.potatocloud.core.networking.packet.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StopServicePacket implements Packet {

    private String serviceName;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(serviceName);
    }

    @Override
    public void read(PacketBuffer buf) {
        serviceName = buf.readString();
    }
}
