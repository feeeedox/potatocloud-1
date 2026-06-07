package net.potatocloud.node.service.start.condition.conditions;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.node.service.start.condition.ServiceStartCondition;

public class MinOnlineCondition implements ServiceStartCondition {

    @Override
    public boolean shouldStart(ServiceGroup group) {
        final long serviceCount = group.getAllServices().stream()
                .filter(service -> service.running() || service.state() == ServiceState.STARTING || service.state() == ServiceState.STOPPING)
                .count();

        return group.getMinOnlineCount() > serviceCount;
    }
}
