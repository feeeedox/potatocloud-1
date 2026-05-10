package net.potatocloud.node.service;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppingEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.packets.service.ServiceRemovePacket;
import net.potatocloud.common.FileUtils;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.runtime.ServiceRuntime;
import net.potatocloud.node.template.TemplateManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final ServiceGroup group;
    private final NodeConfig config;
    private final Logger logger;

    private final List<String> logs;

    private final NetworkServer server;
    private final ScreenManager screenManager;
    private final TemplateManager templateManager;

    private final EventManager eventManager;
    private final ServiceManager serviceManager;
    private final Console console;

    private final Map<String, Property<?>> propertyMap;
    private final Screen screen;

    @Setter
    private int maxPlayers;

    @Setter
    private ServiceStatus status = ServiceStatus.STOPPED;

    private long startTimestamp;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final Path directory;

    private final ServiceRuntime runtime;

    public ServiceImpl(
            ServiceRuntime runtime,
            int serviceId,
            int port,
            ServiceGroup group,
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            ScreenManager screenManager,
            TemplateManager templateManager,
            EventManager eventManager,
            ServiceManager serviceManager,
            Console console
    ) {
        this.runtime = runtime;
        this.serviceId = serviceId;
        this.port = port;
        this.group = group;
        this.config = config;
        this.logger = logger;
        this.logs = new ArrayList<>();
        this.server = server;
        this.screenManager = screenManager;
        this.templateManager = templateManager;
        this.eventManager = eventManager;
        this.serviceManager = serviceManager;
        this.console = console;

        this.maxPlayers = group.getMaxPlayers();
        this.propertyMap = new HashMap<>(group.getPropertyMap());
        this.screen = new Screen(getName());
        this.directory = group.isStatic()
                ? Path.of(config.getStaticFolder()).resolve(getName())
                : Path.of(config.getTempServicesFolder()).resolve(getName() + "-" + UUID.randomUUID());
    }

    @Override
    public String getName() {
        return group.getName() + config.getSplitter() + serviceId;
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
    }

    public int getUsedMemory() {
        return runtime.usedMemory(this);
    }

    public void start() {
        if (isOnline()) {
            return;
        }

        startTimestamp = System.currentTimeMillis();

        screenManager.register(screen);

        runtime.prepare(this);
        runtime.start(this);

        logger.info("Service &a" + getName() + "&7 is now starting&8... &8[&7Port&8: &a" + port + "&8, &7Group&8: &a" + group.getName() + "&8]");
        eventManager.call(new PreparedServiceStartingEvent(getName()));
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        if (status == ServiceStatus.STOPPED || status == ServiceStatus.STOPPING) {
            return CompletableFuture.completedFuture(null);
        }

        status = ServiceStatus.STOPPING;

        logger.info("Service &a" + getName() + "&7 is now stopping&8...");
        eventManager.call(new ServiceStoppingEvent(getName()));

        return CompletableFuture.runAsync(() -> {
            runtime.stop(this);

            synchronized (this) {
                status = ServiceStatus.STOPPED;
            }

            ((ServiceManagerImpl) serviceManager).removeService(this);
            screenManager.unregister(screen.name());

            if (screenManager.getCurrentScreen().name().equals(getName())) {
                screenManager.switchTo(Screen.NODE_SCREEN);
            }

            if (server != null) {
                server.generateBroadcast().broadcast(new ServiceRemovePacket(getName(), getPort()));

                eventManager.call(new ServiceStoppedEvent(getName()));
            }

            logger.info("Service &a" + getName() + " &7has been stopped");
        }, executorService);
    }

    @Override
    public boolean executeCommand(String command) {
        return runtime.executeCommand(this, command);
    }

    @Override
    public void copy(String template, String filter) {
        final Path directory = getDirectory();
        final Path templatesDirectory = Path.of(config.getTemplatesFolder());

        Path targetPath = templatesDirectory.resolve(template);
        Path sourcePath = directory;

        if (filter != null && filter.startsWith("/")) {
            sourcePath = directory.resolve(filter.substring(1));
            targetPath = targetPath.resolve(filter.substring(1));
        }

        if (!Files.exists(sourcePath)) {
            return;
        }

        if (!Files.exists(targetPath)) {
            templateManager.createTemplate(targetPath.toFile().getName());
        }

        FileUtils.copyDirectory(sourcePath, targetPath);
    }

    public void log(String log) {
        logs.add(log);
        screen.addLog(log);

        if (screenManager.getCurrentScreen().name().equals(getName())) {
            console.println(log);
        }
    }

    @Override
    public String getPropertyHolderName() {
        return getName();
    }
}
