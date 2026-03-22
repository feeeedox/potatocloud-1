package net.potatocloud.node.service.start.rule.rules;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.service.start.rule.ServiceStartRule;

public class GroupMaxOnlineRule implements ServiceStartRule {

    @Override
    public boolean allows(ServiceGroup group) {
        final long activeServices = group.getAllServices().stream()
                .filter(service -> service.isOnline() || service.getStatus() == ServiceStatus.STARTING)
                .count();

        return activeServices < group.getMaxOnlineCount();
    }
}
