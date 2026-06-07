package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.RequestServicesPacket;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;

@RequiredArgsConstructor
public class RequestServicesListener implements PacketListener<RequestServicesPacket> {

    private final ServiceManager serviceManager;

    @Override
    public void handle(PacketContext<RequestServicesPacket> ctx) {
        for (Service service : serviceManager.services()) {
            ctx.connection().send(new ServiceAddPacket(service, null));
        }
    }
}
