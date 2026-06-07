package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.events.service.ServiceStartedEvent;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.api.utils.TimeFormatter;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceStartedPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.runtime.ServiceMemoryUpdateTask;
import net.potatocloud.node.service.runtime.ServiceProcessChecker;

@RequiredArgsConstructor
public class ServiceStartedListener implements PacketListener<ServiceStartedPacket> {

    private final ServiceManager serviceManager;
    private final Logger logger;
    private final EventBus eventBus;
    private final ClusterManagerImpl clusterManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<ServiceStartedPacket> ctx) {
        final ServiceStartedPacket packet = ctx.packet();
        final Service service = serviceManager.getService(packet.serviceName());
        if (service == null) {
            return;
        }

        final boolean clustered = Node.getInstance().getConfig().cluster().enabled();
        logger.info("Service &a" + packet.serviceName() + (clustered ? "&7 started on node &a" + service.nodeName() : "&7 started"));

        logger.debug("Service &a" + packet.serviceName() + "&7 took &a" + TimeFormatter.formatAsDuration(System.currentTimeMillis() - service.getStartTimestamp()) + "&7 to start");

        service.setStatus(ServiceStatus.RUNNING);
        service.update();

        if (ctx.connection().type() == ConnectionType.CONNECTOR) {
            clusterManager.broadcast(new ServiceStartedPacket(packet.serviceName()));
        }

        server.broadcast().connectors().send(new ServiceStartedPacket(packet.serviceName()));

        eventBus.publish(new ServiceStartedEvent(packet.serviceName()));

        if (clusterManager.isLocal(service.nodeName())) {
            if (service instanceof AbstractService abstractService) {
                abstractService.setProcessChecker(new ServiceProcessChecker(abstractService));
            }
            new ServiceMemoryUpdateTask(service, Node.getInstance().getServer(), clusterManager);
        }
    }
}
