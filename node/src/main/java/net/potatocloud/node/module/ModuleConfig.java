package net.potatocloud.node.module;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModuleConfig(String name, String version, @JsonProperty("main") String mainClass) {}
