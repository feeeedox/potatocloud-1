package net.potatocloud.common.config;

import net.potatocloud.common.ResourceFileUtils;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig implements Config {

    private final ConfigSource source;
    private final Path path;
    private final ClassLoader classLoader;

    protected JsonNode node;

    protected AbstractConfig(ConfigSource source, Path path) {
        this(source, path, AbstractConfig.class.getClassLoader());
    }

    protected AbstractConfig(ConfigSource source, Path path, ClassLoader classLoader) {
        this.source = source;
        this.path = path;
        this.classLoader = classLoader;
    }

    @Override
    public void load() {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                ResourceFileUtils.copyResourceFile(classLoader, path.getFileName().toString(), path);
            }

            node = source.read(path);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config: " + path.getFileName(), e);
        }
    }

    public void save() {
        if (node == null) {
            return;
        }

        source.write(path, node);
    }

    @Override
    public void reload() {
        load();
    }
}
