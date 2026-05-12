package net.potatocloud.core.networking.packet.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartServicePacket implements Packet {

    private String groupName;
    private String requestId;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(groupName);
        buf.writeString(requestId);
    }

    @Override
    public void read(PacketBuffer buf) {
        groupName = buf.readString();
        requestId = buf.readString();
    }
}
