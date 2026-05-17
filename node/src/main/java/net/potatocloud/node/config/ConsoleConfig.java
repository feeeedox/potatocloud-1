package net.potatocloud.node.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsoleConfig(
        String prompt,
        @JsonProperty("enable-banner") boolean enableBanner,
        @JsonProperty("primary-color") int primaryColorCode,
        @JsonProperty("log-player-connections") boolean logPlayerConnections
) {}