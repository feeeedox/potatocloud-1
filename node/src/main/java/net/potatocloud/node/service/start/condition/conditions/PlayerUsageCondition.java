package net.potatocloud.node.service.start.condition.conditions;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.service.start.condition.ServiceStartCondition;

import java.util.List;

public class PlayerUsageCondition implements ServiceStartCondition {

    @Override
    public boolean shouldStart(ServiceGroup group) {
        final List<Service> activeServices = group.getAllServices().stream()
                .filter(service -> service.isOnline() || service.getStatus() == ServiceStatus.STARTING)
                .toList();

        // If start percentage is disabled, do not start a new service
        if (group.getStartPercentage() == -1) {
            return false;
        }

        final int maxPlayers = activeServices.stream()
                .mapToInt(Service::getMaxPlayers)
                .sum();

        // If there are no available player slots, do not start a service
        if (maxPlayers <= 0) {
            return false;
        }

        // Get the current usage percentage of the service group
        final int usagePercent = (int) ((group.getOnlinePlayerCount() / (double) maxPlayers) * 100);

        return usagePercent >= group.getStartPercentage();
    }
}
