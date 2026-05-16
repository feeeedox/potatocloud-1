package net.potatocloud.node.platform.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VersionConfig(
        @JsonProperty("version")
        String name,
        String url,
        boolean legacy,
        boolean local
) {}