package net.potatocloud.node.platform.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProcessingConfig(@JsonProperty("cache-builder") String cacheBuilder) {}
