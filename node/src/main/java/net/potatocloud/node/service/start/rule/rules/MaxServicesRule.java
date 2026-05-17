package net.potatocloud.node.service.start.rule.rules;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.start.rule.ServiceStartRule;

@RequiredArgsConstructor
public class MaxServicesRule implements ServiceStartRule {

    private final NodeConfig config;
    private final ServiceManager serviceManager;

    @Override
    public boolean allows(ServiceGroup group) {
        final int maxServices = config.service().maxServices();

        // If max services is set to -1 (unlimited), always allow starting new services
        if (maxServices == -1) {
            return true;
        }

        final long activeServices = serviceManager.getAllServices().stream()
                .filter(service -> service.isOnline() || service.getStatus() == ServiceStatus.STARTING)
                .count();

        return activeServices < maxServices;
    }
}
