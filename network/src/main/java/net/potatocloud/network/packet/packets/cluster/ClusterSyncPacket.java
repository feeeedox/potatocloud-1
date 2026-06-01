package net.potatocloud.network.packet.packets.cluster;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ClusterSyncPacket(List<ServiceGroup> groups, List<Service> services, Set<CloudPlayer> players) implements Packet {

    public static final Codec<ClusterSyncPacket> CODEC = new Codec<>() {

        @Override
        public void encode(ClusterSyncPacket packet, PacketBuffer buf) {
            buf.writeInt(packet.groups().size());
            packet.groups().forEach(buf::writeServiceGroup);

            buf.writeInt(packet.services().size());
            packet.services().forEach(buf::writeService);

            buf.writeInt(packet.players().size());
            packet.players().forEach(buf::writeCloudPlayer);
        }

        @Override
        public ClusterSyncPacket decode(PacketBuffer buf) {
            final int groupCount = buf.readInt();
            final List<ServiceGroup> groups = new ArrayList<>(groupCount);
            for (int i = 0; i < groupCount; i++) {
                groups.add(buf.readServiceGroup());
            }

            final int serviceCount = buf.readInt();
            final List<Service> services = new ArrayList<>(serviceCount);
            for (int i = 0; i < serviceCount; i++) {
                services.add(buf.readService());
            }

            final int playerCount = buf.readInt();
            final Set<CloudPlayer> players = new HashSet<>(playerCount);
            for (int i = 0; i < playerCount; i++) {
                players.add(buf.readCloudPlayer());
            }

            return new ClusterSyncPacket(groups, services, players);
        }
    };
}
