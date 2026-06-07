package net.potatocloud.node.config;

import net.potatocloud.common.FileUtils;

import java.nio.file.Path;
import java.util.UUID;

public final class NodeConfigPatcher {

    private NodeConfigPatcher() {
    }

    public static void apply(Path configPath) {
        replaceClusterTokenIfDefault(configPath);
    }

    private static void replaceClusterTokenIfDefault(Path configPath) {
        FileUtils.replaceInFile(configPath, "token: change-me", "token: " + UUID.randomUUID().toString().replace("-", ""));
    }
}