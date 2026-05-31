package net.potatocloud.network.packet.packets.player;

import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.UUID;

public record CloudPlayerRemovePacket(UUID playerUniqueId) implements Packet {

    public static final Codec<CloudPlayerRemovePacket> CODEC = new Codec<>() {

        @Override
        public void encode(CloudPlayerRemovePacket packet, PacketBuffer buf) {
            buf.writeUUID(packet.playerUniqueId());
        }

        @Override
        public CloudPlayerRemovePacket decode(PacketBuffer buf) {
            return new CloudPlayerRemovePacket(buf.readUUID());
        }
    };
}