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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private final ServiceLauncher launcher;
    private final NetworkServer server;
    private final Logger logger;
    private final NodeConfig config;
    private final ClusterManagerImpl clusterManager;

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
    public Optional<Service> find(String name) {
        return services.stream().filter(service -> service.name().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<Service> services() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public void update(Service service) {
        final ServiceUpdatePacket packet = new ServiceUpdatePacket(
                service.name(),
                service.state().name(),
                service.maxPlayers(),
                service.getPropertyMap()
        );
        server.broadcast().connectors().send(packet);
        clusterManager.broadcast(packet);
    }

    @Override
    public CompletableFuture<Service> start(ServiceGroup group) {
        if (group == null) {
            return CompletableFuture.completedFuture(null);
        }

        return group.node()
                .map(node -> {
                    if (!clusterManager.isLocal(node.name())) {
                        clusterManager.sendTo(node.name(),new StartServicePacket(group.name(), null));
                        return null;
                    }

                    return launcher.start(group.name(), null);
                })
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<Void> stop(Service service) {
        // todo
        if (!clusterManager.isLocal(service.node().get().name())) {
            clusterManager.sendTo(service.node().get().name(), new StopServicePacket(service.name()));
            return CompletableFuture.completedFuture(null);
        }

        if (!(service instanceof AbstractService abstractService)) {
            return CompletableFuture.completedFuture(null);
        }

        return abstractService.shutdown();
    }

    @Override
    public void copyTo(Service service, String template, String filter) {
        if (!clusterManager.isLocal(service.node().get().name())) {
            clusterManager.sendTo(service.node().get().name(), new ServiceCopyPacket(service.name(), template, filter));
            return;
        }

        if (service instanceof AbstractService abstractService) {
            abstractService.copy(template, filter);
        }
    }

    @Override
    public void execute(Service service, String command) {
        // todo
        if (!clusterManager.isLocal(service.node().get().name())) {
            clusterManager.sendTo(service.node().get().name(), new ServiceExecuteCommandPacket(service.name(), command));
            return;
        }

        if (service instanceof AbstractService abstractService) {
            abstractService.executeCommand(command);
        }
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
                .mapToLong(service -> service.group().maxMemory())
                .sum();

        return (usedMb + group.maxMemory()) <= config.service().maxMemory();
    }

    public void logMemoryWarning(ServiceGroup group) {
        final long usedMb = services.stream()
                .mapToLong(service -> service.group().maxMemory())
                .sum();

        logger.warn("Service(s) for group &a" + group.name()
                + " &7could not be started &8[&7Required&8: &a" + group.maxMemory() + " MB"
                + "&8, &7Used&8: &a" + usedMb + " MB"
                + "&8, &7Max&8: &a" + config.service().maxMemory() + " MB&8]");
    }

    @Override
    public Optional<Service> current() {
        throw new UnsupportedOperationException("getCurrentService() is only available when the API is used from within a connector.");
    }
}
