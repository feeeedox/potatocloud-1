package net.potatocloud.node.service.listeners;

import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceStartingPacket;

public class ServiceStartingListener implements PacketListener<ServiceStartingPacket> {

    private final Logger logger;
    private final ServiceManager serviceManager;

    public ServiceStartingListener(Logger logger, ServiceManager serviceManager) {
        this.logger = logger;
        this.serviceManager = serviceManager;
    }

    @Override
    public void handle(PacketContext<ServiceStartingPacket> ctx) {
        final Service service = serviceManager.getService(ctx.packet().serviceName());
        if (service == null) {
            return;
        }

        logger.info("Service &a" + service.getName() + "&7 is starting on Node &a" + service.nodeName()
                + " &8[&7Port&8: &a" + service.getPort()
                + "&8, &7Group&8: &a" + service.getServiceGroup().getName()
        );
    }
}
