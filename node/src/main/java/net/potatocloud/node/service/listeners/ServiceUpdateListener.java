package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.common.PropertyUtil;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceUpdatePacket;

@RequiredArgsConstructor
public class ServiceUpdateListener implements PacketListener<ServiceUpdatePacket> {

    private final ServiceManager serviceManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<ServiceUpdatePacket> ctx) {
        final ServiceUpdatePacket packet = ctx.packet();
        final Service service = serviceManager.getService(packet.serviceName());
        if (service == null) {
            return;
        }

        service.setStatus(ServiceStatus.valueOf(packet.status()));
        service.setMaxPlayers(packet.maxPlayers());
        service.getPropertyMap().clear();
        for (Property<?> property : packet.propertyMap().values()) {
            PropertyUtil.setPropertyUnchecked(service, property);
        }

        server.broadcast().connectors().exclude(ctx.connection()).send(packet);
    }
}
