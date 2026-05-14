package net.potatocloud.node.service.local;

import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.PrepareStep;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.common.FileUtils;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.PlatformPrepareSteps;
import net.potatocloud.node.platform.PlatformUtils;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.config.ServicePerformanceFlags;
import net.potatocloud.node.template.TemplateManager;
import oshi.ffm.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalService extends AbstractService {

    private final DownloadManager downloadManager;
    private final CacheManager cacheManager;

    private Process process;
    private OSProcess osProcess;

    private BufferedWriter processWriter;
    private BufferedReader processReader;

    public LocalService(
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
            Console console,
            DownloadManager downloadManager,
            CacheManager cacheManager
    ) {
        super(serviceId,
                port,
                group,
                config,
                logger,
                server,
                screenManager,
                templateManager,
                eventBus,
                serviceManager,
                console
        );

        this.downloadManager = downloadManager;
        this.cacheManager = cacheManager;
    }

    @Override
    protected void prepare() {
        final Platform platform = group.getPlatform();
        final PlatformVersion version = group.getPlatformVersion();

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create service directory: " + directory, e);
        }

        for (String template : group.getServiceTemplates()) {
            templateManager.copyTemplate(template, directory);
        }

        final Path pluginsFolder = directory.resolve("plugins");
        try {
            Files.createDirectories(pluginsFolder);

            final String pluginName = platformPluginName();
            final Path dataDirectory = Path.of(config.getDataFolder());

            Files.copy(
                    dataDirectory.resolve(pluginName),
                    pluginsFolder.resolve(pluginName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to set up plugins folder for service " + name, e);
        }

        downloadManager.downloadPlatformVersion(platform, platform.getVersion(group.getPlatformVersionName()));

        final Path cacheDirectory = cacheManager.preCachePlatform(group);
        cacheManager.copyCacheToService(group, cacheDirectory, directory);

        try {
            Files.copy(
                    PlatformUtils.getPlatformJarPath(platform, version),
                    directory.resolve("server.jar"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy platform jar for service" + name, e);
        }

        for (String stepName : platform.getPrepareSteps()) {
            final PrepareStep step = PlatformPrepareSteps.getStep(stepName);

            if (step != null) {
                step.execute(this, platform, directory);
            }
        }
    }

    @Override
    protected void startProcess() {
        final List<String> startArguments = startArguments();

        try {
            process = new ProcessBuilder(startArguments)
                    .directory(directory.toFile())
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server process for service " + name, e);
        }

        osProcess = new SystemInfo().getOperatingSystem().getProcess((int) process.pid());

        processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        Thread.startVirtualThread(() -> {
            try {
                String line;
                while (process.isAlive() && (line = processReader.readLine()) != null) {
                    log(line);
                }
            } catch (IOException ignored) {
            }
        });
    }

    @Override
    protected void stopProcess() {
        processChecker.close();

        if (process != null) {
            executeCommand("stop");

            try {
                final boolean hasStopped = process.waitFor(config.getKillTimeout(), TimeUnit.SECONDS);
                if (!hasStopped) {
                    logger.debug("Service &a" + name + " &7did not stop in time, destroying process&8...");

                    process.destroyForcibly();
                    process.waitFor();
                }

            } catch (Exception e) {
                logger.error("Failed to stop service &a" + name + "&8: &7" + e.getMessage());
            }

            process = null;

            if (!group.isStatic() && Files.exists(directory)) {
                FileUtils.deleteDirectory(directory);
            }
        }
    }

    @Override
    public boolean alive() {
        return process != null && process.isAlive();
    }

    @Override
    public int getUsedMemory() {
        if (!alive()) {
            return 0;
        }

        if (osProcess == null) {
            return 0;
        }

        return (int) (osProcess.getResidentMemory() / 1024 / 1024);
    }

    @Override
    public boolean executeCommand(String command) {
        if (!alive() || processWriter == null) {
            return false;
        }

        try {
            processWriter.write(command);
            processWriter.newLine();
            processWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to send command to service " + name);
            return false;
        }

        return true;
    }

    private List<String> startArguments() {
        final List<String> args = new ArrayList<>();
        args.add(group.getJavaCommand());
        args.add("-Xms" + group.getMaxMemory() + "M");
        args.add("-Xmx" + group.getMaxMemory() + "M");
        args.add("-Dpotatocloud.service.name=" + name);
        args.add("-Dpotatocloud.node.port=" + config.getNodePort());

        args.addAll(ServicePerformanceFlags.DEFAULT_FLAGS);

        if (group.getCustomJvmFlags() != null) {
            args.addAll(group.getCustomJvmFlags());
        }

        args.add("-jar");
        args.add(directory.resolve("server.jar").toAbsolutePath().toString());

        if (group.getPlatform().isBukkitBased() && !group.getPlatformVersion().isLegacy()) {
            args.add("-nogui");
        }

        if (group.getPlatform().isLimboBased()) {
            args.add("--nogui");
        }

        return args;
    }
}
