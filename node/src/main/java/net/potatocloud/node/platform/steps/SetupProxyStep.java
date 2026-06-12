package net.potatocloud.node.platform.steps;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.common.config.yaml.YamlConfig;
import net.potatocloud.node.platform.AbstractPrepareStep;
import net.potatocloud.node.platform.VelocityForwardingSecret;
import net.potatocloud.node.utils.PropertiesFileUtils;
import net.potatocloud.node.utils.ProxyUtils;

import java.nio.file.Path;
import java.util.Properties;

public class SetupProxyStep extends AbstractPrepareStep {

    @Override
    public void execute(String serviceName, Platform platform, Path serverDirectory) {
        try {
            // Skip if Bukkit based uses legacy proxy mode (already configured in the spigot.yml)
            if (platform.bukkitBased() && !ProxyUtils.isProxyModernForwarding()) {
                return;
            }

            // Configure Paper for modern Velocity forwarding
            if (platform.paperBased() && ProxyUtils.isProxyModernForwarding()) {
                final YamlConfig config = new YamlConfig(serverDirectory.resolve("config").resolve("paper-global.yml"));
                config.load();

                config.set("proxies.velocity.enabled", true);
                config.set("proxies.velocity.secret", VelocityForwardingSecret.FORWARDING_SECRET);

                config.save();
                return;
            }

            // Configure Limbo proxy settings
            if (platform.limboBased()) {
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SetupProxyStep for service: " + serviceName, e);
        }
    }

    @Override
    public String name() {
        return "setup-proxy";
    }
}
