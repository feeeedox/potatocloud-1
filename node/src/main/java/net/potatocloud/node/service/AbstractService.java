package net.potatocloud.node.service;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppingEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.common.FileUtils;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.packets.service.ServiceRemovePacket;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.template.TemplateManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public abstract class AbstractService implements Service {

    private final int serviceId;
    private final int port;
    protected final ServiceGroup group;
    protected final NodeConfig config;
    protected final Logger logger;

    protected final String name;

    @Setter
    protected ServiceProcessChecker processChecker;

    private final ExecutorService executorService;

    @Setter
    private ServiceStatus status = ServiceStatus.STOPPED;

    private long startTimestamp;

    private final NetworkServer server;
    private final EventBus eventBus;
    private final ServiceManager serviceManager;
    private final Console console;

    private final ScreenManager screenManager;
    private final Screen screen;
    private final List<String> logs = new ArrayList<>();

    protected final TemplateManager templateManager;
    protected final Path directory;

    private final Map<String, Property<?>> propertyMap;

    @Setter
    private int maxPlayers;

    public AbstractService(
            int serviceId,
            int port,
            ServiceGroup group,
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            ScreenManager screenManager,
            TemplateManager templateManager,
            EventBus eventBus,
            ServiceManager serviceManager,
            Console console
    ) {
        this.serviceId = serviceId;
        this.port = port;
        this.group = group;
        this.config = config;
        this.logger = logger;

        this.server = server;
        this.eventBus = eventBus;
        this.serviceManager = serviceManager;
        this.console = console;
        this.screenManager = screenManager;
        this.templateManager = templateManager;
        this.name = group.getName() + config.getSplitter() + serviceId;
        this.screen = new Screen(name);
        this.directory = group.isStatic()
                ? Path.of(config.getStaticFolder()).resolve(name)
                : Path.of(config.getTempServicesFolder()).resolve(name + "-" + UUID.randomUUID());
        this.maxPlayers = group.getMaxPlayers();
        this.propertyMap = new HashMap<>(group.getPropertyMap());
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
    }

    public void start() {
        if (isOnline()) {
            return;
        }

        startTimestamp = System.currentTimeMillis();

        screenManager.register(screen);

        status = ServiceStatus.PREPARING;

        prepare();

        status = ServiceStatus.STARTING;

        startProcess();

        logger.info("Service &a" + name + "&7 is now starting&8... &8[&7Port&8: &a" + port + "&8, &7Group&8: &a" + group.getName() + "&8]");
        eventBus.publish(new PreparedServiceStartingEvent(name));
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        if (status == ServiceStatus.STOPPED || status == ServiceStatus.STOPPING) {
            return CompletableFuture.completedFuture(null);
        }

        status = ServiceStatus.STOPPING;

        logger.info("Service &a" + name + "&7 is now stopping&8...");
        eventBus.publish(new ServiceStoppingEvent(name));

        return CompletableFuture.runAsync(() -> {
            stopProcess();

            ((ServiceManagerImpl) serviceManager).removeService(this);
            screenManager.unregister(screen.name());

            if (screenManager.getCurrentScreen().name().equals(name)) {
                screenManager.switchTo(Screen.NODE_SCREEN);
            }

            if (server != null) {
                server.generateBroadcast().broadcast(new ServiceRemovePacket(name, getPort()));
                eventBus.publish(new ServiceStoppedEvent(name));
            }

            synchronized (this) {
                status = ServiceStatus.STOPPED;
            }

            logger.info("Service &a" + name + " &7has been stopped");
        }, executorService);
    }

    @Override
    public void copy(String template, String filter) {
        final Path templatesDirectory = Path.of(config.getTemplatesFolder());

        Path sourcePath = directory;
        Path targetPath = templatesDirectory.resolve(template);

        if (filter != null && filter.startsWith("/")) {
            sourcePath = directory.resolve(filter.substring(1));
            targetPath = targetPath.resolve(filter.substring(1));
        }

        if (!Files.exists(sourcePath)) {
            return;
        }

        if (!Files.exists(targetPath)) {
            templateManager.createTemplate(targetPath.getFileName().toString());
        }

        FileUtils.copyDirectory(sourcePath, targetPath);
    }

    protected void log(String log) {
        logs.add(log);
        screen.addLog(log);

        if (screenManager.getCurrentScreen().name().equals(name)) {
            console.println(log);
        }
    }

    @Override
    public String getPropertyHolderName() {
        return name;
    }

    protected String platformPluginName() {
        final Platform platform = group.getPlatform();
        final PlatformVersion version = group.getPlatformVersion();

        if (platform.isBukkitBased()) {
            return version.isLegacy()
                    ? "potatocloud-plugin-spigot-legacy.jar"
                    : "potatocloud-plugin-spigot.jar";
        } else if (platform.isVelocityBased()) {
            return "potatocloud-plugin-velocity.jar";
        } else if (platform.isLimboBased()) {
            return "potatocloud-plugin-limbo.jar";
        } else {
            logger.error("No Plugin found for platform " + platform.getName());
            return "";
        }
    }

    protected abstract void prepare();

    protected abstract void startProcess();

    protected abstract void stopProcess();

    public abstract boolean alive();

}