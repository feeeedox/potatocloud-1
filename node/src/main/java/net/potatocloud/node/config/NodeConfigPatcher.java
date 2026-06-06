package net.potatocloud.node.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class NodeConfigPatcher {

    private NodeConfigPatcher() {
    }

    public static void apply(Path configPath) {
        replaceClusterTokenIfDefault(configPath);
    }

    private static void replaceClusterTokenIfDefault(Path configPath) {
        replaceInFile(configPath,"token: change-me", "token: " + UUID.randomUUID().toString().replace("-", ""));
    }

    private static void replaceInFile(Path path, String target, String replacement) {
        try {
            final String content = Files.readString(path, StandardCharsets.UTF_8);
            if (!content.contains(target)) {
                return;
            }

            Files.writeString(
                    path,
                    content.replace(target, replacement),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to update " + path.getFileName(), e);
        }
    }
}