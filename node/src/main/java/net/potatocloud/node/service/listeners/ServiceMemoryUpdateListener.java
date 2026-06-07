package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.impl.ServiceImpl;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceMemoryUpdatePacket;

@RequiredArgsConstructor
public class ServiceMemoryUpdateListener implements PacketListener<ServiceMemoryUpdatePacket> {

    private final ServiceManager serviceManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<ServiceMemoryUpdatePacket> ctx) {
        if (ctx.connection().type() != ConnectionType.NODE) {
            return;
        }

        final Service service = serviceManager.getService(ctx.packet().serviceName());
        if (service == null) {
            return;
        }

        if (service instanceof ServiceImpl serviceImpl) {
            serviceImpl.setUsedMemory(ctx.packet().usedMemory());
        }

        server.broadcast().connectors().send(ctx.packet());
    }
}
