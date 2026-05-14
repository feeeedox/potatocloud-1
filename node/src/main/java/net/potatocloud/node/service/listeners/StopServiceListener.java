package net.potatocloud.node.service.listeners;

import lombok.AllArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.StopServicePacket;

@AllArgsConstructor
public class StopServiceListener implements PacketListener<StopServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void handle(PacketContext<StopServicePacket> ctx) {
        final Service service = serviceManager.getService(ctx.packet().serviceName());
        if (service == null) {
            return;
        }
        service.shutdown();
    }
}
