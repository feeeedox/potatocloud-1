package net.potatocloud.node.service.start;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.events.property.PropertyChangedEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.DefaultProperties;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.ServiceManagerImpl;
import net.potatocloud.node.service.start.condition.ServiceStartCondition;
import net.potatocloud.node.service.start.condition.conditions.MinOnlineCondition;
import net.potatocloud.node.service.start.condition.conditions.PlayerUsageCondition;
import net.potatocloud.node.service.start.rule.ServiceStartRule;
import net.potatocloud.node.service.start.rule.rules.GroupMaxOnlineRule;
import net.potatocloud.node.service.start.rule.rules.MaxMemoryRule;
import net.potatocloud.node.service.start.rule.rules.MaxServicesRule;
import net.potatocloud.node.service.start.rule.rules.MaxStartingRule;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceStartScheduler {

    private final NodeConfig config;

    private final ServiceGroupManager groupManager;
    private final ServiceManagerImpl serviceManager;

    private final List<ServiceStartRule> rules;
    private final List<ServiceStartCondition> conditions;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());

    public ServiceStartScheduler(NodeConfig config, ServiceGroupManager groupManager, ServiceManagerImpl serviceManager, EventBus eventBus) {
        this.config = config;
        this.groupManager = groupManager;
        this.serviceManager = serviceManager;

        this.rules = List.of(
                new GroupMaxOnlineRule(),
                new MaxServicesRule(config, serviceManager),
                new MaxStartingRule(config, serviceManager),
                new MaxMemoryRule(serviceManager)
        );

        this.conditions = List.of(
                new MinOnlineCondition(),
                new PlayerUsageCondition()
        );

        // Handle game state changes
        eventBus.subscribe(PropertyChangedEvent.class, event -> {
            if (!event.propertyName().equals(DefaultProperties.GAME_STATE.getName())) {
                return;
            }

            if (event.newValue() == null || !event.newValue().equals("INGAME")) {
                return;
            }

            serviceManager.find(event.holderName()).ifPresent(service -> {
                final ServiceGroup group = service.group();
                final int onlineServices = group.getAllServices().stream()
                        .mapToInt(Service::playerCount)
                        .sum();

                if (onlineServices >= group.getMaxOnlineCount()) {
                    return;
                }

                serviceManager.start(service.group());
            });

        });
    }

    public void start() {
        executor.scheduleAtFixedRate(this::run, 0, 1, TimeUnit.SECONDS);
    }

    private void run() {
        groupManager.getAllServiceGroups().stream()
                .filter(group -> groupManager.existsServiceGroup(group.getName()))
                .filter(this::isLocalNode)
                .sorted(Comparator.comparingInt(ServiceGroup::getStartPriority).reversed())
                .forEach(group -> {
                    if (rules.stream().allMatch(rule -> rule.allows(group)) && conditions.stream().anyMatch(condition -> condition.shouldStart(group))) {
                        serviceManager.start(group);
                    }
                });
    }

    private boolean isLocalNode(ServiceGroup group) {
        if (!config.cluster().enabled()) {
            return true;
        }
        final String nodeName = group.nodeName();
        return nodeName == null || nodeName.equals(config.cluster().name());
    }

    public void close() {
        executor.shutdownNow();
    }
}
