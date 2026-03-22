package net.potatocloud.node.service.start.condition.conditions;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.service.start.condition.ServiceStartCondition;

public class MinOnlineCondition implements ServiceStartCondition {

    @Override
    public boolean shouldStart(ServiceGroup group) {
        final long serviceCount = group.getAllServices().stream()
                .filter(service -> service.isOnline() || service.getStatus() == ServiceStatus.STARTING || service.getStatus() == ServiceStatus.STOPPING)
                .count();

        return group.getMinOnlineCount() > serviceCount;
    }
}
