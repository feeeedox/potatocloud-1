package net.potatocloud.connector.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.connector.service.ServiceImpl;
import net.potatocloud.connector.service.ServiceManagerImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ServiceAddListener implements PacketListener<ServiceAddPacket> {

    private final ServiceManagerImpl serviceManager;

    @Override
    public void handle(PacketContext<ServiceAddPacket> ctx) {
        final ServiceAddPacket packet = ctx.packet();

        final Service service = new ServiceImpl(
                packet.serviceName(),
                packet.serviceId(),
                packet.port(),
                packet.startTimestamp(),
                CloudAPI.getInstance().getServiceGroupManager().getServiceGroup(packet.groupName()),
                packet.propertyMap(),
                ServiceStatus.valueOf(packet.status()),
                packet.maxPlayers(),
                0
        );

        final List<Service> services = serviceManager.getAllServices();
        if (!services.contains(service)) {
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
