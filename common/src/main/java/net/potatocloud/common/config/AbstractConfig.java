package net.potatocloud.common.config;

import net.potatocloud.common.ResourceFileUtils;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig implements Config {

    private final ConfigSource source;
    private final String fileName;
    private final Path path;

    protected JsonNode node;

    protected AbstractConfig(ConfigSource source, String directory, String fileName) {
        this.source = source;
        this.fileName = fileName;
        this.path = Path.of(directory).resolve(fileName);
    }

    @Override
    public void load() {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                ResourceFileUtils.copyResourceFile(path.getFileName().toString(), path);
            }

            node = source.read(path);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config: " + fileName, e);
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
