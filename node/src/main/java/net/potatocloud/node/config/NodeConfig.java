package net.potatocloud.node.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NodeConfig(
        ConsoleConfig console,
        ServiceConfig service,
        FoldersConfig folders,
        NodeNetworkConfig node,
        @JsonProperty("disable-update-checker") boolean disableUpdateChecker,
        boolean debug
) {}
