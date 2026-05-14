package net.potatocloud.node.platform.steps;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.node.platform.AbstractPrepareStep;
import net.potatocloud.node.platform.VelocityForwardingSecret;
import net.potatocloud.node.utils.PropertiesFileUtils;
import net.potatocloud.node.utils.ProxyUtils;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class SetupProxyStep extends AbstractPrepareStep {

    @Override
    public void execute(String serviceName, Platform platform, Path serverDirectory) {
        try {
            // Skip if Bukkit based uses legacy proxy mode (already configured in the spigot.yml)
            if (platform.isBukkitBased() && !ProxyUtils.isProxyModernForwarding()) {
                return;
            }

            // Configure Paper for modern Velocity forwarding
            if (platform.isPaperBased() && ProxyUtils.isProxyModernForwarding()) {
                final Path paperYml = serverDirectory.resolve("config").resolve("paper-global.yml");
                final YamlFile yaml = new YamlFile(paperYml.toFile());

                yaml.load();
                yaml.set("proxies.velocity.enabled", true);
                yaml.set("proxies.velocity.secret", VelocityForwardingSecret.FORWARDING_SECRET);
                yaml.save();

                return;
            }

            // Configure Limbo proxy settings
            if (platform.isLimboBased()) {
                final Path propertiesPath = serverDirectory.resolve("server.properties");
                final Properties properties = PropertiesFileUtils.loadProperties(propertiesPath);

                if (ProxyUtils.isProxyModernForwarding()) {
                    properties.setProperty("forwarding-secrets", VelocityForwardingSecret.FORWARDING_SECRET);
                    properties.setProperty("velocity-modern", "true");
                } else {
                    properties.setProperty("bungeecord", "true");
                }

                PropertiesFileUtils.saveProperties(properties, propertiesPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute SetupProxyStep for service: " + serviceName, e);
        }
    }

    @Override
    public String getName() {
        return "setup-proxy";
    }
}
