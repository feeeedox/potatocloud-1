package net.potatocloud.node.platform.steps;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PrepareStep;
import net.potatocloud.api.service.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EulaStep implements PrepareStep {

    @Override
    public void execute(Service service, Platform platform, Path serverDirectory) {
        try {
            if (platform.isBukkitBased()) {
                final Path eulaFile = serverDirectory.resolve("eula.txt");

                Files.writeString(eulaFile, "eula=true", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute EulaStep for service: " + service.getName(), e);
        }
    }

    @Override
    public String getName() {
        return "eula";
    }
}
