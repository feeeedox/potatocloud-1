package net.potatocloud.core.networking.packet.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlayerRemovePacket implements Packet {

    private UUID playerUniqueId;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(playerUniqueId.toString());
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUniqueId = UUID.fromString(buf.readString());
    }
}
