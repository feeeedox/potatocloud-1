package net.potatocloud.node.service;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppingEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.common.FileUtils;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.ServiceRemovePacket;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.prepare.ServicePreparer;
import net.potatocloud.node.service.runtime.ServiceProcessChecker;
import net.potatocloud.node.service.runtime.ServiceRuntime;
import net.potatocloud.node.template.TemplateManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractService implements Service {

    private final int serviceId;
    private final int port;
    protected final ServiceGroup group;
    protected final String name;
    protected final Path directory;

    protected final NodeConfig config;
    protected final Logger logger;

    private final NetworkServer server;
    private final EventBus eventBus;
    private final ServiceManager serviceManager;
    protected final TemplateManager templateManager;
    private final ScreenManager screenManager;

    private final Screen screen;
    private final Console console;
    private final List<String> logs = new ArrayList<>();
    private final Map<String, Property<?>> propertyMap;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final ServicePreparer preparer;
    private final ServiceRuntime runtime;

    protected ServiceProcessChecker processChecker;

    private ServiceStatus status = ServiceStatus.STOPPED;

    private long startTimestamp;

    private int maxPlayers;

    protected AbstractService(
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
            ServiceRuntime runtime
    ) {
        this.serviceId = serviceId;
        this.port = port;
        this.group = group;
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.eventBus = eventBus;
        this.serviceManager = serviceManager;
        this.templateManager = templateManager;
        this.screenManager = screenManager;
        this.console = console;
        this.preparer = preparer;
        this.runtime = runtime;
        this.name = group.getName() + config.service().splitter() + serviceId;
        this.screen = new Screen(name);
        this.directory = resolveDirectory();
        this.maxPlayers = group.getMaxPlayers();
        this.propertyMap = new HashMap<>(group.getPropertyMap());
    }

    public void start() {
        if (isOnline()) {
            return;
        }

        startTimestamp = System.currentTimeMillis();
        screenManager.register(screen);

        status = ServiceStatus.PREPARING;
        preparer.prepare(directory, name, port);

        status = ServiceStatus.STARTING;
        runtime.start(directory, this);

        logger.info("Service &a" + name + "&7 is now starting&8... "
                + "&8[&7Port&8: &a" + port
                + "&8, &7Group&8: &a" + group.getName() + "&8]"
        );

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
            if (processChecker != null) {
                processChecker.close();
            }

            runtime.stop();

            ((ServiceManagerImpl) serviceManager).removeService(this);
            screenManager.unregister(screen.name());

            if (screenManager.getCurrentScreen().name().equals(name)) {
                screenManager.switchTo(Screen.NODE_SCREEN);
            }

            server.generateBroadcast().broadcast(new ServiceRemovePacket(name, getPort()));
            eventBus.publish(new ServiceStoppedEvent(name));

            if (!group.isStatic() && Files.exists(directory)) {
                FileUtils.deleteDirectory(directory);
            }

            synchronized (this) {
                status = ServiceStatus.STOPPED;
            }

            logger.info("Service &a" + name + " &7has been stopped");
        }, executorService);
    }

    @Override
    public void copy(String template, String filter) {
        final Path templatesDirectory = Path.of(config.folders().templates());

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getServiceId() {
        return serviceId;
    }

    @Override
    public ServiceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int getUsedMemory() {
        return runtime.usedMemory();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
    }

    @Override
    public boolean executeCommand(String command) {
        return runtime.executeCommand(command);
    }

    @Override
    public Map<String, Property<?>> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public String getPropertyHolderName() {
        return name;
    }

    public boolean alive() {
        return runtime.alive();
    }

    public Logger getLogger() {
        return logger;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setProcessChecker(ServiceProcessChecker processChecker) {
        this.processChecker = processChecker;
    }


    public void log(String log) {
        logs.add(log);
        screen.addLog(log);
        // TODO Remove console
        if (screenManager.getCurrentScreen().name().equals(name)) {
            console.println(log);
        }
    }

    private Path resolveDirectory() {
        if (group.isStatic()) {
            return Path.of(config.folders().staticServices()).resolve(name);
        }
        return Path.of(config.folders().tempServices()).resolve(name + "-" + UUID.randomUUID());
    }
}