package net.potatocloud.node.service.start.rule.rules;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.service.start.rule.ServiceStartRule;

@RequiredArgsConstructor
public class MaxServicesRule implements ServiceStartRule {

    private final ServiceManager serviceManager;

    @Override
    public boolean allows(ServiceGroup group) {
        // TODO Update to config value
        final int maxServices = 5;

        final long activeServices = serviceManager.getAllServices().stream()
                .filter(service -> service.isOnline() || service.getStatus() == ServiceStatus.STARTING)
                .count();

        return activeServices < maxServices;
    }
}
