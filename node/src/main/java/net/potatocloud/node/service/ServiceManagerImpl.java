package net.potatocloud.node.service;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.*;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.config.ServiceDefaultFiles;
import net.potatocloud.node.service.listeners.*;
import net.potatocloud.node.service.runtime.ServiceLauncher;
import net.potatocloud.node.template.TemplateManager;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private final ServiceLauncher launcher;
    private final NetworkServer server;
    private final Logger logger;
    private final NodeConfig config;
    private final ClusterManagerImpl clusterManager;
    private final ServiceGroupManager groupManager;

    public ServiceManagerImpl(
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            EventBus eventBus,
            ServiceGroupManager groupManager,
            ScreenManager screenManager,
            TemplateManager templateManager,
            DownloadManager downloadManager,
            CacheManager cacheManager,
            ClusterManagerImpl clusterManager
    ) {
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.clusterManager = clusterManager;
        this.groupManager = groupManager;

        ServiceDefaultFiles.copyDefaultFiles(Path.of(config.folders().data()));

        final ServiceFactory factory = new ServiceFactory(config, logger, server, eventBus, this, screenManager, templateManager, downloadManager, cacheManager, clusterManager);

        this.launcher = new ServiceLauncher(this, groupManager, factory, config, server, clusterManager);

        server.on(RequestServicesPacket.class, new RequestServicesListener(this));
        server.on(ServiceAddPacket.class, new ServiceAddListener(this, server));
        server.on(ServiceRemovePacket.class, new ServiceRemoveListener(this, server));
        server.on(ServiceStartedPacket.class, new ServiceStartedListener(this, logger, eventBus, clusterManager, server));
        server.on(ServiceUpdatePacket.class, new ServiceUpdateListener(this, server, clusterManager));
        server.on(ServiceStartingPacket.class, new ServiceStartingListener(logger, this));
        server.on(StartServicePacket.class, new StartServiceListener(this, groupManager, clusterManager));
        server.on(StopServicePacket.class, new StopServiceListener(this, clusterManager));
        server.on(ServiceExecuteCommandPacket.class, new ServiceExecuteCommandListener(this, clusterManager));
        server.on(ServiceCopyPacket.class, new ServiceCopyListener(this, clusterManager));
        server.on(ServiceMemoryUpdatePacket.class, new ServiceMemoryUpdateListener(this, server));
    }

    @Override
    public Service getService(String name) {
        return services.stream()
                .filter(service -> service.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Service> getAllServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public void updateService(Service service) {
        final ServiceUpdatePacket packet = new ServiceUpdatePacket(
                service.getName(),
                service.getStatus().name(),
                service.getMaxPlayers(),
                service.getPropertyMap()
        );
        server.broadcast().connectors().send(packet);
        clusterManager.broadcast(packet);
    }

    @Override
    public void startService(String groupName) {
        final ServiceGroup group = groupManager.getServiceGroup(groupName);
        if (group == null) {
            return;
        }

        if (!clusterManager.isLocal(group.nodeName())) {
            clusterManager.sendTo(group.nodeName(), new StartServicePacket(groupName, null));
            return;
        }

        launcher.start(groupName, null);
    }

    @Override
    public CompletableFuture<Service> startServiceAsync(String groupName) {
        final ServiceGroup group = groupManager.getServiceGroup(groupName);
        if (group == null) {
            return CompletableFuture.completedFuture(null);
        }

        if (!clusterManager.isLocal(group.nodeName())) {
            clusterManager.sendTo(group.nodeName(), new StartServicePacket(groupName, null));
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.completedFuture(launcher.start(groupName, null));
    }

    @Override
    public CompletableFuture<Void> stopService(String name) {
        final Service service = getService(name);
        if (service == null) {
            return CompletableFuture.completedFuture(null);
        }

        if (!clusterManager.isLocal(service.nodeName())) {
            clusterManager.sendTo(service.nodeName(), new StopServicePacket(name));
            return CompletableFuture.completedFuture(null);
        }

        if (!(service instanceof AbstractService abstractService)) {
            return CompletableFuture.completedFuture(null);
        }

        return abstractService.shutdown();
    }

    @Override
    public boolean executeCommand(String name, String command) {
        final Service service = getService(name);
        if (service == null) {
            return false;
        }

        if (!clusterManager.isLocal(service.nodeName())) {
            clusterManager.sendTo(service.nodeName(), new ServiceExecuteCommandPacket(name, command));
            return true;
        }

        return service.executeCommand(command);
    }

    @Override
    public void copy(String name, String template, String filter) {
        final Service service = getService(name);
        if (service == null) {
            return;
        }

        if (!clusterManager.isLocal(service.nodeName())) {
            clusterManager.sendTo(service.nodeName(), new ServiceCopyPacket(name, template, filter));
            return;
        }

        service.copy(template, filter);
    }

    public void startServiceInternal(String groupName, String requestId) {
        launcher.start(groupName, requestId);
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
    }

    public boolean hasEnoughMemory(ServiceGroup group) {
        if (!config.service().memoryCheckEnabled()) {
            return true;
        }

        final long usedMb = services.stream()
                .mapToLong(service -> service.getServiceGroup().getMaxMemory())
                .sum();

        return (usedMb + group.getMaxMemory()) <= config.service().maxMemory();
    }

    public void logMemoryWarning(ServiceGroup group) {
        final long usedMb = services.stream()
                .mapToLong(service -> service.getServiceGroup().getMaxMemory())
                .sum();

        logger.warn("Service(s) for group &a" + group.getName()
                + " &7could not be started &8[&7Required&8: &a" + group.getMaxMemory() + " MB"
                + "&8, &7Used&8: &a" + usedMb + " MB"
                + "&8, &7Max&8: &a" + config.service().maxMemory() + " MB&8]");
    }

    @Override
    public Service getCurrentService() {
        throw new UnsupportedOperationException("getCurrentService() is only available when the API is used from within a connector.");
    }
}
