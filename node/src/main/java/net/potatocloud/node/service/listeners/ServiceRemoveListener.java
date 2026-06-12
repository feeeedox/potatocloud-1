package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceRemovePacket;
import net.potatocloud.node.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class ServiceRemoveListener implements PacketListener<ServiceRemovePacket> {

    private final ServiceManagerImpl serviceManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<ServiceRemovePacket> ctx) {
        serviceManager.find(ctx.packet().serviceName()).ifPresent(serviceManager::removeService);
        server.broadcast().connectors().exclude(ctx.connection()).send(ctx.packet());
    }
}
