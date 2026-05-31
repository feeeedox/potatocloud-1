package net.potatocloud.node.version;

import net.potatocloud.api.version.Version;
import net.potatocloud.common.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class VersionFile {

    public static final Path VERSION_FILE = Paths.get("").toAbsolutePath().resolve(".version");

    private VersionFile() {
    }

    public static Version read() {
        if (!Files.exists(VERSION_FILE)) {
            return null;
        }
        try {
            final String content = Files.readString(VERSION_FILE, StandardCharsets.UTF_8).strip();
            return Version.fromString(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read version file: " + VERSION_FILE, e);
        }
    }

    public static void write(Version version) {
        try {
            if (!Files.exists(VERSION_FILE)) {
                FileUtils.createHiddenFile(VERSION_FILE);
            }

            Files.writeString(VERSION_FILE, version.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write version file: " + VERSION_FILE, e);
        }
    }
}