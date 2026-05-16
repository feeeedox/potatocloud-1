package net.potatocloud.common.config.yaml;

import net.potatocloud.common.JacksonUtils;
import net.potatocloud.common.config.ConfigSource;
import tools.jackson.databind.JsonNode;

import java.nio.file.Path;

public class YamlSource implements ConfigSource {

    @Override
    public JsonNode read(Path path) {
        return JacksonUtils.JSON_MAPPER.readTree(path);
    }

    @Override
    public void write(Path path, JsonNode node) {
        JacksonUtils.JSON_MAPPER.writeValue(path, node);
    }

    @Override
    public String fileExtension() {
        return "yml";
    }
}
