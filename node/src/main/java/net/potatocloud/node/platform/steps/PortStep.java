package net.potatocloud.node.platform.steps;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.node.platform.AbstractPrepareStep;
import net.potatocloud.node.utils.PropertiesFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PortStep extends AbstractPrepareStep {

    @Override
    public void execute(String serviceName, Platform platform, Path serverDirectory) {
        try {
            final int port = (int) data().get("port");

            if (platform.isBukkitBased()) {
                final Path propertiesPath = serverDirectory.resolve("server.properties");
                final Properties properties = PropertiesFileUtils.loadProperties(propertiesPath);

                properties.setProperty("server-port", String.valueOf(port));
                properties.setProperty("query.port", String.valueOf(port));

                PropertiesFileUtils.saveProperties(properties, propertiesPath);
                return;
            }

            if (platform.isProxy() && platform.isVelocityBased()) {
                final Path velocityToml = serverDirectory.resolve("velocity.toml");

                String fileContent = Files.readString(velocityToml);
                fileContent = fileContent.replace(
                        "bind = \"0.0.0.0:25565\"",
                        "bind = \"0.0.0.0:" + port + "\""
                );

                Files.writeString(velocityToml, fileContent);
            }

            if (platform.isLimboBased()) {
                final Path propertiesPath = serverDirectory.resolve("server.properties");
                final Properties properties = PropertiesFileUtils.loadProperties(propertiesPath);

                properties.setProperty("server-port", String.valueOf(port));

                PropertiesFileUtils.saveProperties(properties, propertiesPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute PortStep for service: " + serviceName, e);
        }
    }

    @Override
    public String getName() {
        return "port";
    }
}
