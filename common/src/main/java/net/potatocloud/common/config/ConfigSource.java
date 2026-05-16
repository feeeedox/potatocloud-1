package net.potatocloud.common.config;

import tools.jackson.databind.JsonNode;

import java.nio.file.Path;

public interface ConfigSource {

    JsonNode read(Path path);

    void write(Path path, JsonNode node);

    String fileExtension();

}
