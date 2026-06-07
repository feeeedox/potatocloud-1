package net.potatocloud.node.cluster.listeners;

import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.event.EventPacket;
import net.potatocloud.node.cluster.ClusterEventBus;

public class ClusterEventListener implements PacketListener<EventPacket> {

    private final ClusterEventBus eventBus;

    public ClusterEventListener(ClusterEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(PacketContext<EventPacket> ctx) {
        if (ctx.connection().type() != ConnectionType.NODE) {
            return;
        }
        eventBus.publishEventFromCluster(ctx.packet());
    }
}
