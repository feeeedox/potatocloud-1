package net.potatocloud.node.service.helper;

import net.potatocloud.api.group.Group;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.utils.NetworkUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ServicePorts {

    private ServicePorts() {
    }

    public static int nextPort(Group group, NodeConfig config, List<Service> services) {
        int port = group.platform().proxy()
                ? config.service().proxyStartPort()
                : config.service().serviceStartPort();

        final Set<Integer> usedPorts = services.stream()
                .map(Service::port)
                .collect(Collectors.toSet());

        while (usedPorts.contains(port) || !NetworkUtils.isPortFree(port)) {
            port++;
        }
        return port;
    }

}
