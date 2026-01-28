package net.potatocloud.node.group;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.property.Property;
import org.simpleyaml.configuration.file.YamlFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ServiceGroupStorage {

    @SneakyThrows
    public void saveToFile(ServiceGroup group, Path directory) {
        final YamlFile config = new YamlFile(directory.resolve(group.getName() + ".yml").toFile());

        config.set("name", group.getName());
        config.set("platform", group.getPlatformName());
        config.set("platform-version", group.getPlatformVersionName());
        config.set("java-command", group.getJavaCommand());

        if (!group.getCustomJvmFlags().isEmpty()) {
            config.set("jvm-flags", group.getCustomJvmFlags());
        }

        config.set("max-players", group.getMaxPlayers());
        config.set("max-memory", group.getMaxMemory());
        config.set("min-online-count", group.getMinOnlineCount());
        config.set("max-online-count", group.getMaxOnlineCount());

        config.set("static", group.isStatic());
        config.set("fallback", group.isFallback());
        config.set("start-priority", group.getStartPriority());
        config.set("start-percentage", group.getStartPercentage());

        config.set("templates", group.getServiceTemplates());

        if (!group.getProperties().isEmpty()) {
            for (Property<?> property : group.getProperties()) {
                config.set("properties." + property.getName() + ".value", property.getValue());
                config.set("properties." + property.getName() + ".default", property.getDefaultValue());
            }
        }

        config.save();
    }

    @SneakyThrows
    public ServiceGroup loadFromFile(Path groupFile) {
        final YamlFile config = new YamlFile(groupFile.toFile());
        config.load();

        final Map<String, Property<?>> properties = new HashMap<>();
        if (config.isSet("properties")) {
            for (String key : config.getConfigurationSection("properties").getKeys(false)) {
                final Object value = config.get("properties." + key + ".value");
                Object defaultValue = config.get("properties." + key + ".default");
                if (defaultValue == null) {
                    defaultValue = value;
                }

                properties.put(key, Property.of(key, defaultValue, value));
            }
        }

        return new ServiceGroupImpl(
                config.getString("name"),
                config.getString("platform"),
                config.getString("platform-version"),
                config.getStringList("templates"),
                config.getInt("min-online-count"),
                config.getInt("max-online-count"),
                config.getInt("max-players"),
                config.getInt("max-memory"),
                config.getBoolean("fallback"),
                config.getBoolean("static"),
                config.getInt("start-priority"),
                config.getInt("start-percentage"),
                config.getString("java-command"),
                config.getStringList("jvm-flags"),
                properties
        );
    }
}
