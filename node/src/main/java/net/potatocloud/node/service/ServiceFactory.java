package net.potatocloud.node.service;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.node.Node;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.impl.LocalService;
import net.potatocloud.node.service.prepare.LocalServicePreparer;
import net.potatocloud.node.service.prepare.ServicePreparer;
import net.potatocloud.node.service.runtime.LocalServiceRuntime;
import net.potatocloud.node.service.runtime.ServiceRuntime;
import net.potatocloud.node.template.TemplateManager;

public final class ServiceFactory {

    private final NodeConfig config;
    private final Logger logger;
    private final NetworkServer server;
    private final EventBus eventBus;
    private final ServiceManager serviceManager;
    private final ScreenManager screenManager;
    private final TemplateManager templateManager;

    private final DownloadManager downloadManager;
    private final CacheManager cacheManager;

    public ServiceFactory(
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            EventBus eventBus,
            ServiceManager serviceManager,
            ScreenManager screenManager,
            TemplateManager templateManager,
            DownloadManager downloadManager,
            CacheManager cacheManager
    ) {
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.eventBus = eventBus;
        this.serviceManager = serviceManager;
        this.screenManager = screenManager;
        this.templateManager = templateManager;
        this.downloadManager = downloadManager;
        this.cacheManager = cacheManager;
    }

    public AbstractService create(ServiceType type, int serviceId, int port, ServiceGroup group) {
        return switch (type) {
            case LOCAL -> {
                final ServicePreparer preparer = new LocalServicePreparer(
                        group, config, logger, templateManager, downloadManager, cacheManager
                );
                final ServiceRuntime runtime = new LocalServiceRuntime(group, config, logger);
                yield new LocalService(
                        serviceId, port, group,
                        config, logger, server, eventBus, serviceManager, templateManager,
                        screenManager, Node.getInstance().getConsole(), preparer, runtime // TODO Remove console
                );
            }
        };
    }
}