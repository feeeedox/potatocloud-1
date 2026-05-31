package net.potatocloud.node.config;

import java.util.List;

public record ClusterConfig(boolean enabled, String name, List<String> nodes) {

}
