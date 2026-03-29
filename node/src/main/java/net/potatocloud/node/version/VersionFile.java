package net.potatocloud.node.version;

import net.potatocloud.api.utils.version.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

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
                Files.createFile(VERSION_FILE);

                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Files.setAttribute(VERSION_FILE, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
                }
            }

            Files.writeString(VERSION_FILE, version.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write version file: " + VERSION_FILE, e);
        }
    }
}