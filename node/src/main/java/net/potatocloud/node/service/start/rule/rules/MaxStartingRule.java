package net.potatocloud.node.service.start.rule.rules;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.service.start.rule.ServiceStartRule;

@RequiredArgsConstructor
public class MaxStartingRule implements ServiceStartRule {

    private final ServiceManager serviceManager;

    @Override
    public boolean allows(ServiceGroup group) {
        // TODO Update to config value
        final int maxStarting = 5;

        final long startingServices = serviceManager.getAllServices().stream()
                .filter(service -> service.getStatus() == ServiceStatus.STARTING)
                .count();

        return startingServices < maxStarting;
    }
}
