package net.potatocloud.node.platform.config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlatformsConfig(@JsonProperty("platforms") Map<String, PlatformConfig> platforms) {

    public PlatformsConfig {
        platforms = platforms != null ? new LinkedHashMap<>(platforms) : new LinkedHashMap<>();
    }
}