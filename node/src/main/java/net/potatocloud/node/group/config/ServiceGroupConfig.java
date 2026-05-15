package net.potatocloud.node.group.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.property.Property;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ServiceGroupConfig(
        String name,

        String platform,

        @JsonProperty("platform-version")
        String platformVersion,

        @JsonProperty("java-command")
        String javaCommand,

        @JsonProperty("jvm-flags")
        List<String> jvmFlags,

        @JsonProperty("max-players")
        int maxPlayers,

        @JsonProperty("max-memory")
        int maxMemory,

        @JsonProperty("min-online-count")
        int minOnlineCount,

        @JsonProperty("max-online-count")
        int maxOnlineCount,

        @JsonProperty("static")
        boolean isStatic,

        boolean fallback,

        @JsonProperty("start-priority")
        int startPriority,

        @JsonProperty("start-percentage")
        int startPercentage,

        List<String> templates,

        List<PropertyConfig> properties
) {

    public static ServiceGroupConfig from(ServiceGroup group) {
        return new ServiceGroupConfig(
                group.getName(),
                group.getPlatformName(),
                group.getPlatformVersionName(),
                group.getJavaCommand(),
                group.getCustomJvmFlags(),
                group.getMaxPlayers(),
                group.getMaxMemory(),
                group.getMinOnlineCount(),
                group.getMaxOnlineCount(),
                group.isStatic(),
                group.isFallback(),
                group.getStartPriority(),
                group.getStartPercentage(),
                group.getServiceTemplates(),
                group.getProperties().stream()
                        .map(PropertyConfig::from)
                        .toList()
        );
    }


    public ServiceGroup toGroup() {
        final Set<String> names = new HashSet<>();
        final Map<String, Property<?>> propertyMap = new HashMap<>();

        for (PropertyConfig property : properties) {
            final String name = property.name();

            // Check for duplicate names
            if (!names.add(name)) {
                throw new IllegalStateException("Duplicate property " + name + " found in group " + this.name);
            }

            propertyMap.put(name, property.toProperty());
        }

        return new ServiceGroupImpl(
                name,
                platform,
                platformVersion,
                javaCommand,
                jvmFlags,
                maxPlayers,
                maxMemory,
                minOnlineCount,
                maxOnlineCount,
                isStatic,
                fallback,
                startPriority,
                startPercentage,
                templates,
                propertyMap
        );
    }
}