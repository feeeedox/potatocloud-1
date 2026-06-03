package net.potatocloud.node.service.impl;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.prepare.ServicePreparer;
import net.potatocloud.node.service.runtime.ServiceRuntime;
import net.potatocloud.node.template.TemplateManager;

public final class LocalService extends AbstractService {

    public LocalService(
            int serviceId,
            int port,
            ServiceGroup group,
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            EventBus eventBus,
            ServiceManager serviceManager,
            TemplateManager templateManager,
            ScreenManager screenManager,
            Console console,
            ServicePreparer preparer,
            ServiceRuntime runtime,
            ClusterManagerImpl clusterManager
    ) {
        super(serviceId, port, group, config, logger, server, eventBus, serviceManager, templateManager, screenManager, console, preparer, runtime, clusterManager);
    }
}