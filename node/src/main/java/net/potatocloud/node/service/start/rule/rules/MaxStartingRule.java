package net.potatocloud.node.service.start.rule.rules;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.start.rule.ServiceStartRule;

@RequiredArgsConstructor
public class MaxStartingRule implements ServiceStartRule {

    private final NodeConfig config;
    private final ServiceManager serviceManager;

    @Override
    public boolean allows(ServiceGroup group) {
        final int maxStarting = config.service().maxStartingServices();

        // If max starting services is set to -1 (unlimited), always allow starting new services
        if (maxStarting == -1) {
            return true;
        }

        final long startingServices = serviceManager.getAllServices().stream()
                .filter(service -> service.getStatus() == ServiceStatus.STARTING)
                .count();

        return startingServices < maxStarting;
    }
}
