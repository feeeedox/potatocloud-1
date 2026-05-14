package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceCopyPacket;

@RequiredArgsConstructor
public class ServiceCopyListener implements PacketListener<ServiceCopyPacket> {

    private final ServiceManager serviceManager;

    @Override
    public void handle(PacketContext<ServiceCopyPacket> ctx) {
        final ServiceCopyPacket packet = ctx.packet();
        final Service service = serviceManager.getService(packet.serviceName());
        if (service == null) {
            return;
        }
        service.copy(packet.templateName(), packet.filter());
    }
}
