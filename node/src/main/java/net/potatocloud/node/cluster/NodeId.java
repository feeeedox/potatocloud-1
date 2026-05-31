package net.potatocloud.node.cluster;

import net.potatocloud.common.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public final class NodeId {

    private static final Path ID_FILE = Paths.get("").toAbsolutePath().resolve(".node-id");

    private NodeId() {
    }

    public static UUID load() {
        if (Files.exists(ID_FILE)) {
            try {
                return UUID.fromString(Files.readString(ID_FILE, StandardCharsets.UTF_8).strip());
            } catch (Exception ignored) {
            }
        }
        return generate();
    }

    private static UUID generate() {
        final UUID id = UUID.randomUUID();
        try {
            if (!Files.exists(ID_FILE)) {
                FileUtils.createHiddenFile(ID_FILE);
            }

            Files.writeString(ID_FILE, id.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save node ID", e);
        }
        return id;
    }
}
