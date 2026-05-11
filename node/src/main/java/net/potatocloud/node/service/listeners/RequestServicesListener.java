package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.service.RequestServicesPacket;
import net.potatocloud.core.networking.packet.packets.service.ServiceAddPacket;

@RequiredArgsConstructor
public class RequestServicesListener implements PacketListener<RequestServicesPacket> {

    private final ServiceManager serviceManager;

    @Override
    public void handle(PacketContext<RequestServicesPacket> ctx) {
        for (Service service : serviceManager.getAllServices()) {
            ctx.connection().send(new ServiceAddPacket(
                    service.getName(),
                    service.getServiceId(),
                    service.getPort(),
                    service.getStartTimestamp(),
                    service.getServiceGroup().getName(),
                    service.getPropertyMap(),
                    service.getStatus().name(),
                    service.getMaxPlayers(),
                    null
            ));
        }
    }
}
