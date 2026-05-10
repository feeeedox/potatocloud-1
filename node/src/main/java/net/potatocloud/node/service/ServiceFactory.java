package net.potatocloud.node.service;

import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.local.LocalService;
import net.potatocloud.node.template.TemplateManager;

public class ServiceFactory {

    private final NodeConfig config;
    private final Logger logger;
    private final NetworkServer server;
    private final ScreenManager screenManager;
    private final TemplateManager templateManager;
    private final EventManager eventManager;
    private final ServiceManager serviceManager;
    private final Console console;
    private final DownloadManager downloadManager;
    private final CacheManager cacheManager;

    public ServiceFactory(
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            ScreenManager screenManager,
            TemplateManager templateManager,
            EventManager eventManager,
            ServiceManager serviceManager,
            Console console,
            DownloadManager downloadManager,
            CacheManager cacheManager
    ) {
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.screenManager = screenManager;
        this.templateManager = templateManager;
        this.eventManager = eventManager;
        this.serviceManager = serviceManager;
        this.console = console;
        this.downloadManager = downloadManager;
        this.cacheManager = cacheManager;
    }

    public AbstractService create(
            ServiceType type,
            int serviceId,
            int port,
            ServiceGroup group
    ) {

        return switch (type) {

            case LOCAL -> new LocalService(
                    serviceId,
                    port,
                    group,
                    config,
                    logger,
                    server,
                    screenManager,
                    templateManager,
                    eventManager,
                    serviceManager,
                    console,
                    downloadManager,
                    cacheManager
            );
        };
    }
}