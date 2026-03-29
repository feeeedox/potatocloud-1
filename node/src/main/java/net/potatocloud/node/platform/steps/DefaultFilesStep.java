package net.potatocloud.node.platform.steps;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PrepareStep;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.utils.ProxyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFilesStep implements PrepareStep {

    @Override
    public void execute(Service service, Platform platform, Path serverDirectory) {
        try {
            final NodeConfig config = Node.getInstance().getConfig();

            if (platform.isBukkitBased()) {
                final Path serverProperties = serverDirectory.resolve("server.properties");
                if (!serverProperties.toFile().exists()) {
                    Files.copy(Path.of(config.getDataFolder(), "server.properties"), serverProperties);
                }

                // The spigot yml is only needed when velocity uses legacy forwarding
                if (!ProxyUtils.isProxyModernForwarding()) {
                    final Path spigotYml = serverDirectory.resolve("spigot.yml");

                    if (!Files.exists(spigotYml)) {
                        Files.copy(Path.of(config.getDataFolder(), "spigot.yml"), spigotYml);
                    }
                }

                if (platform.isPaperBased() && ProxyUtils.isProxyModernForwarding()) {
                    final Path paperYml = serverDirectory.resolve("config").resolve("paper-global.yml");

                    if (!Files.exists(paperYml)) {
                        Files.createDirectories(paperYml.getParent());
                        Files.copy(Path.of(config.getDataFolder(), "paper-global.yml"), paperYml);
                    }
                }
                return;
            }

            if (platform.isVelocityBased()) {
                final Path velocityToml = serverDirectory.resolve("velocity.toml");
                if (!Files.exists(velocityToml)) {
                    Files.copy(Path.of(config.getDataFolder(), "velocity.toml"), velocityToml);
                    return;
                }
            }

            if (platform.isLimboBased()) {
                final Path serverProperties = serverDirectory.resolve("server.properties");

                if (!Files.exists(serverProperties)) {
                    Files.copy(Path.of(config.getDataFolder(), "limbo-server.properties"), serverProperties);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute DefaultFilesStep for service: " + service.getName(), e);
        }
    }

    @Override
    public String getName() {
        return "default-files";
    }
}
