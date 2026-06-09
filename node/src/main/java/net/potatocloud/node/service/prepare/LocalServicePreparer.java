package net.potatocloud.node.service.prepare;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.PrepareStep;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.platform.DownloadManager;
import net.potatocloud.node.platform.PlatformPrepareSteps;
import net.potatocloud.node.platform.PlatformUtils;
import net.potatocloud.node.platform.cache.CacheManager;
import net.potatocloud.node.template.TemplateManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class LocalServicePreparer implements ServicePreparer {

    private final ServiceGroup group;
    private final NodeConfig config;
    private final Logger logger;
    private final TemplateManager templateManager;
    private final DownloadManager downloadManager;
    private final CacheManager cacheManager;

    public LocalServicePreparer(
            ServiceGroup group,
            NodeConfig config,
            Logger logger,
            TemplateManager templateManager,
            DownloadManager downloadManager,
            CacheManager cacheManager
    ) {
        this.group = group;
        this.config = config;
        this.logger = logger;
        this.templateManager = templateManager;
        this.downloadManager = downloadManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public void prepare(Path directory, String serviceName, int port) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create service directory: " + directory, e);
        }

        for (String template : group.templates()) {
            templateManager.copyTemplate(template, directory);
        }

        final Path pluginsFolder = directory.resolve("plugins");
        try {
            Files.createDirectories(pluginsFolder);

            final String pluginName = resolvePluginName();
            if (pluginName.isEmpty()) {
                return;
            }

            Files.copy(
                    Path.of(config.folders().data()).resolve(pluginName),
                    pluginsFolder.resolve(pluginName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to install plugin for service " + serviceName, e);
        }

        final Platform platform = group.platform();
        downloadManager.downloadPlatformVersion(platform, platform.version(group.platformVersion().name()).get());

        final Path cacheDirectory = cacheManager.preCachePlatform(group);
        cacheManager.copyCacheToService(group, cacheDirectory, directory);

        final PlatformVersion version = group.platformVersion();
        try {
            Files.copy(
                    PlatformUtils.getPlatformJarPath(platform, version),
                    directory.resolve("server.jar"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy server jar for service " + serviceName, e);
        }

        for (String stepName : group.platform().prepareSteps()) {
            final PrepareStep step = PlatformPrepareSteps.getStep(stepName);
            if (step != null) {
                step.data().put("group", group);
                step.data().put("port", port);

                step.execute(serviceName, group.platform(), directory);
            }
        }

    }

    private String resolvePluginName() {
        final Platform platform = group.platform();
        final PlatformVersion version = group.platformVersion();

        if (platform.bukkitBased()) {
            return version.legacy()
                    ? "potatocloud-plugin-spigot-legacy.jar"
                    : "potatocloud-plugin-spigot.jar";
        }

        if (platform.velocityBased()) {
            return "potatocloud-plugin-velocity.jar";
        }
        if (platform.limboBased()) {
            return "potatocloud-plugin-limbo.jar";
        }

        logger.error("No plugin found for platform " + platform.name());
        return "";
    }
}
