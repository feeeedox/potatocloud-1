package net.potatocloud.node.service.helper;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ServiceIds {

    private ServiceIds() {
    }

    public static int nextId(ServiceGroup group, List<Service> services) {
        final Set<Integer> usedIds = services.stream()
                .filter(service -> service.getServiceGroup().equals(group))
                .map(Service::getServiceId)
                .collect(Collectors.toSet());

        int id = 1;
        while (usedIds.contains(id)) {
            id++;
        }
        return id;
    }
}
