package net.potatocloud.common.config.yaml;

import net.potatocloud.common.config.AbstractConfig;
import net.potatocloud.common.config.ConfigNode;
import net.potatocloud.common.config.JacksonConfigNode;
import tools.jackson.databind.JsonNode;

public class YamlConfig extends AbstractConfig {

    public YamlConfig(String directory, String fileName) {
        super(new YamlSource(), directory, fileName);
    }

    @Override
    public ConfigNode get(String path) {
        return new JacksonConfigNode(resolve(path));
    }

    private JsonNode resolve(String path) {
        if (this.node == null) {
            return null;
        }

        JsonNode node = this.node;
        for (String key : path.split("\\.")) {
            if (!node.isObject()) {
                return null;
            }

            node = node.get(key);

            if (node == null) {
                return null;
            }
        }
        return node;
    }
}
