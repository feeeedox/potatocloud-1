package net.potatocloud.plugins.shared;

import net.kyori.adventure.text.Component;
import net.potatocloud.common.config.Config;
import net.potatocloud.common.config.yaml.YamlConfig;

public class MessagesConfig {

    private final Config config;

    public MessagesConfig(String folder) {
        this.config = new YamlConfig(folder, "messages.yml");
    }

    public void load() {
        config.load();
    }

    public void reload() {
        config.reload();
    }

    public Component get(String key) {
        return get(key, true);
    }

    public Component get(String path, boolean withPrefix) {
        final String prefix = withPrefix ? config.get("prefix").asString() : "";
        final String message = config.get(path).asString();
        return MessageUtils.format(prefix + message);
    }
}
