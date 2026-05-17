package net.potatocloud.node.service.helper;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.utils.NetworkUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ServicePorts {

    private ServicePorts() {
    }

    public static int nextPort(ServiceGroup group, NodeConfig config, List<Service> services) {
        int port = group.getPlatform().isProxy()
                ? config.service().proxyStartPort()
                : config.service().serviceStartPort();

        final Set<Integer> usedPorts = services.stream()
                .map(Service::getPort)
                .collect(Collectors.toSet());

        while (usedPorts.contains(port) || !NetworkUtils.isPortFree(port)) {
            port++;
        }
        return port;
    }

}
