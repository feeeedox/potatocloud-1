package net.potatocloud.node.service.runtime;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.ServiceFactory;
import net.potatocloud.node.service.ServiceManagerImpl;
import net.potatocloud.node.service.ServiceType;
import net.potatocloud.node.service.helper.ServiceIds;
import net.potatocloud.node.service.helper.ServicePorts;

import java.util.List;

public final class ServiceLauncher {

    private final ServiceManagerImpl serviceManager;
    private final ServiceGroupManager groupManager;
    private final ServiceFactory factory;
    private final NodeConfig config;
    private final NetworkServer server;

    public ServiceLauncher(
            ServiceManagerImpl serviceManager,
            ServiceGroupManager groupManager,
            ServiceFactory factory,
            NodeConfig config,
            NetworkServer server
    ) {
        this.serviceManager = serviceManager;
        this.groupManager = groupManager;
        this.factory = factory;
        this.config = config;
        this.server = server;
    }

    public Service start(String groupName, String requestId) {
        final ServiceGroup group = groupManager.getServiceGroup(groupName);
        if (group == null) {
            return null;
        }

        final List<Service> services = serviceManager.getAllServices();

        final int serviceId = ServiceIds.nextId(group, services);
        final int port = ServicePorts.nextPort(group, config, services);
        final AbstractService service = factory.create(ServiceType.LOCAL, serviceId, port, group);

        serviceManager.addService(service);

        server.generateBroadcast().broadcast(new ServiceAddPacket(
                service.getName(),
                service.getServiceId(),
                service.getPort(),
                service.getStartTimestamp(),
                service.getServiceGroup().getName(),
                service.getPropertyMap(),
                service.getStatus().name(),
                service.getMaxPlayers(),
                requestId
        ));

        service.start();
        return service;
    }
}
