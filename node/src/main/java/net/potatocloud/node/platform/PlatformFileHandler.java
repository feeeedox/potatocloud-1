package net.potatocloud.node.platform;

import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PlatformImpl;
import net.potatocloud.api.platform.impl.PlatformVersionImpl;
import net.potatocloud.common.JacksonUtils;
import net.potatocloud.common.ResourceFileUtils;
import net.potatocloud.node.platform.config.*;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlatformFileHandler {

    private static final String FILE_NAME = "platforms.yml";

    private static final YAMLMapper MAPPER = JacksonUtils.YAML_MAPPER;

    private final Logger logger;
    private final Path file;

    public PlatformFileHandler(Logger logger) {
        this.logger = logger;
        this.file = Path.of(FILE_NAME);

        if (!Files.exists(file)) {
            ResourceFileUtils.copyResourceFile(FILE_NAME, file);
        }

        mergeWithDefaults();
    }

    public List<Platform> loadPlatformsFile() {
        final PlatformsConfig root = readFile();
        final List<Platform> platforms = new ArrayList<>();

        for (Map.Entry<String, PlatformConfig> entry : root.platforms().entrySet()) {
            final String name = entry.getKey();
            final PlatformConfig config = entry.getValue();

            final FlagsConfig flags = config.flags();
            final DownloadConfig download = config.download();
            final ProcessingConfig processing = config.processing();

            final PlatformImpl platform = new PlatformImpl(
                    name,
                    download != null ? download.url() : null,
                    flags != null && flags.custom(),
                    flags != null && flags.proxy(),
                    config.base() != null ? config.base() : "UNKNOWN",
                    processing != null ? processing.cacheBuilder() : null,
                    download != null ? download.parser() : "",
                    download != null ? download.hash() : "",
                    config.prepareSteps()
            );

            if (config.versions() == null) {
                logger.warn("No versions found for platform " + name);
                platforms.add(platform);
                continue;
            }

            for (VersionConfig version : config.versions()) {
                platform.versions().add(new PlatformVersionImpl(
                        name,
                        version.name(),
                        version.local(),
                        version.url(),
                        version.legacy()
                ));
            }

            platforms.add(platform);
        }

        return platforms;
    }

    public void savePlatform(Platform platform) {
        final PlatformsConfig root = readFile();
        root.platforms().put(platform.name(), PlatformConfig.from(platform));
        writeFile(root);
    }

    public void deletePlatform(Platform platform) {
        final PlatformsConfig root = readFile();
        root.platforms().remove(platform.name());
        writeFile(root);
    }

    private void mergeWithDefaults() {
        if (!Files.exists(file)) {
            return;
        }

        // Load default from resources
        PlatformsConfig defaults = loadDefaultConfig();
        if (defaults == null) {
            return;
        }

        final PlatformsConfig user = readFile();

        // Merge user custom platforms with defaults
        final Map<String, PlatformConfig> merged = new LinkedHashMap<>(defaults.platforms());
        for (Map.Entry<String, PlatformConfig> entry : user.platforms().entrySet()) {
            if (entry.getValue().flags() != null && entry.getValue().flags().custom()) {
                merged.put(entry.getKey(), entry.getValue());
            }
        }

        // Replace current user config with merged
        writeFile(new PlatformsConfig(merged));
    }

    private PlatformsConfig loadDefaultConfig() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (stream == null) {
                return null;
            }
            return MAPPER.readValue(stream, PlatformsConfig.class);
        } catch (IOException e) {
            logger.error("Failed to copy " + FILE_NAME + " file");
            return null;
        }
    }

    private PlatformsConfig readFile() {
        return MAPPER.readValue(file, PlatformsConfig.class);
    }

    private void writeFile(PlatformsConfig config) {
        MAPPER.writeValue(file, config);
    }
}

