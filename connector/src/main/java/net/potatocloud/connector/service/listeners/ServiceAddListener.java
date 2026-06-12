package net.potatocloud.connector.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.connector.service.ServiceManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ServiceAddListener implements PacketListener<ServiceAddPacket> {

    private final ServiceManagerImpl serviceManager;

    @Override
    public void handle(PacketContext<ServiceAddPacket> ctx) {
        final ServiceAddPacket packet = ctx.packet();
        final Service service = packet.service();

        if (serviceManager.find(service.name()).isEmpty()) {
            serviceManager.addService(service);
        }

        final String requestId = packet.requestId();
        if (requestId == null) {
            return;
        }

        final CompletableFuture<Service> future = serviceManager.getPendingStarts().remove(requestId);
        if (future != null) {
            future.complete(service);
        }
    }
}
