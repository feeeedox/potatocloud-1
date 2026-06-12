package net.potatocloud.node.service.runtime;

import net.potatocloud.api.group.Group;
import net.potatocloud.api.group.GroupManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.ServiceAddPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.ServiceFactory;
import net.potatocloud.node.service.ServiceManagerImpl;
import net.potatocloud.node.service.ServiceType;
import net.potatocloud.node.service.helper.ServiceIds;
import net.potatocloud.node.service.helper.ServicePorts;

import java.util.List;
import java.util.Optional;

public final class ServiceLauncher {

    private final ServiceManagerImpl serviceManager;
    private final GroupManager groupManager;
    private final ServiceFactory factory;
    private final NodeConfig config;
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    public ServiceLauncher(
            ServiceManagerImpl serviceManager,
            GroupManager groupManager,
            ServiceFactory factory,
            NodeConfig config,
            NetworkServer server,
            ClusterManagerImpl clusterManager
    ) {
        this.serviceManager = serviceManager;
        this.groupManager = groupManager;
        this.factory = factory;
        this.config = config;
        this.server = server;
        this.clusterManager = clusterManager;
    }

    public Service start(String groupName, String requestId) {
        final Optional<Group> group = groupManager.find(groupName);
        if (group.isEmpty()) {
            return null;
        }

        final List<Service> services = serviceManager.services();

        final int serviceId = ServiceIds.nextId(group.get(), services);
        final int port = ServicePorts.nextPort(group.get(), config, services);
        final AbstractService service = factory.create(ServiceType.LOCAL, serviceId, port, group.get());

        serviceManager.addService(service);

        server.broadcast().connectors().send(new ServiceAddPacket(service, requestId));
        clusterManager.broadcast(new ServiceAddPacket(service, null));

        service.start();
        return service;
    }
}
