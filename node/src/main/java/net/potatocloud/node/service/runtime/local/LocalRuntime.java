package net.potatocloud.node.service.runtime.local;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.PrepareStep;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.common.FileUtils;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.PlatformPrepareSteps;
import net.potatocloud.node.platform.PlatformUtils;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.service.ServiceImpl;
import net.potatocloud.node.service.config.ServicePerformanceFlags;
import net.potatocloud.node.service.runtime.AbstractServiceRuntime;
import net.potatocloud.node.template.TemplateManager;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalRuntime extends AbstractServiceRuntime {

    private final Logger logger;
    private final NodeConfig config;
    private final TemplateManager templateManager;
    private final DownloadManager downloadManager;
    private final CacheManager cacheManager;

    private Process process;
    private OSProcess osProcess;
    private BufferedWriter processWriter;
    private BufferedReader processReader;

    public LocalRuntime(Logger logger, NodeConfig config, TemplateManager templateManager, DownloadManager downloadManager, CacheManager cacheManager) {
        this.logger = logger;
        this.config = config;
        this.templateManager = templateManager;
        this.downloadManager = downloadManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public void prepare(ServiceImpl service) {
        service.setStatus(ServiceStatus.PREPARING);

        final Path directory = service.getDirectory();
        final ServiceGroup group = service.getServiceGroup();
        final Platform platform = group.getPlatform();
        final PlatformVersion version = group.getPlatformVersion();

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create service directory: " + directory, e);
        }

        for (String template : service.getServiceGroup().getServiceTemplates()) {
            templateManager.copyTemplate(template, directory);
        }

        final Path pluginsFolder = directory.resolve("plugins");
        try {
            Files.createDirectories(pluginsFolder);

            final String pluginName = platformPluginName(service);
            final Path dataDirectory = Path.of(config.getDataFolder());

            Files.copy(
                    dataDirectory.resolve(pluginName),
                    pluginsFolder.resolve(pluginName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to set up plugins folder for service " + service.getName(), e);
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
            throw new RuntimeException("Failed to copy platform jar for service" + service.getName(), e);
        }

        for (String stepName : platform.getPrepareSteps()) {
            final PrepareStep step = PlatformPrepareSteps.getStep(stepName);

            if (step != null) {
                step.execute(service, platform, directory);
            }
        }
    }

    @Override
    public void start(ServiceImpl service) {
        service.setStatus(ServiceStatus.STARTING);

        final List<String> startArguments = startArguments(service);
        final Path directory = service.getDirectory();

        try {
            process = new ProcessBuilder(startArguments)
                    .directory(directory.toFile())
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server process for service " + service.getName(), e);
        }

        osProcess = new SystemInfo().getOperatingSystem().getProcess((int) process.pid());

        processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        Thread.startVirtualThread(() -> {
            try {
                String line;
                while (process.isAlive() && (line = processReader.readLine()) != null) {
                    service.log(line);
                }
            } catch (IOException ignored) {
            }
        });
    }

    @Override
    public void stop(ServiceImpl service) {
        final ServiceGroup group = service.getServiceGroup();
        final Path directory = service.getDirectory();

        service.getProcessChecker().close();

        if (process != null) {
            executeCommand(service, "stop");

            try {
                final boolean hasStopped = process.waitFor(config.getKillTimeout(), TimeUnit.SECONDS);
                if (!hasStopped) {
                    logger.debug("Service &a" + service.getName() + " &7did not stop in time, destroying process&8...");

                    process.destroyForcibly();
                    process.waitFor();
                }

            } catch (Exception e) {
                logger.error("Failed to stop service &a" + service.getName() + "&8: &7" + e.getMessage());
            }

            process = null;

            if (!group.isStatic() && Files.exists(directory)) {
                FileUtils.deleteDirectory(directory);
            }
        }
    }

    @Override
    public boolean executeCommand(ServiceImpl service, String command) {
        if (!alive(service) || processWriter == null) {
            return false;
        }

        try {
            processWriter.write(command);
            processWriter.newLine();
            processWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to send command to service " + service.getName());
            return false;
        }

        return true;
    }

    @Override
    public boolean alive(ServiceImpl service) {
        return process != null && process.isAlive();
    }

    @Override
    public int usedMemory(ServiceImpl service) {
        if (!alive(service)) {
            return 0;
        }

        if (osProcess == null) {
            return 0;
        }

        return (int) (osProcess.getResidentSetSize() / 1024 / 1024);
    }

    private List<String> startArguments(ServiceImpl service) {
        final ServiceGroup group = service.getServiceGroup();
        final Path directory = service.getDirectory();

        final List<String> args = new ArrayList<>();
        args.add(group.getJavaCommand());
        args.add("-Xms" + group.getMaxMemory() + "M");
        args.add("-Xmx" + group.getMaxMemory() + "M");
        args.add("-Dpotatocloud.service.name=" + service.getName());
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
