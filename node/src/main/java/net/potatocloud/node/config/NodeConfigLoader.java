package net.potatocloud.node.config;

import net.potatocloud.common.JacksonUtils;
import net.potatocloud.common.ResourceFileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public final class NodeConfigLoader {

    private static final String CONFIG_FILE_NAME = "config.yml";

    private final Path configPath;

    public NodeConfigLoader() {
        this.configPath = Path.of(CONFIG_FILE_NAME);
    }

    public NodeConfig load() {
        if (!Files.exists(configPath)) {
            ResourceFileUtils.copyResourceFile(CONFIG_FILE_NAME, configPath);
        }
        return JacksonUtils.YAML_MAPPER.readValue(configPath.toFile(), NodeConfig.class);
    }

    public NodeConfig reload() {
        return load();
    }
}
