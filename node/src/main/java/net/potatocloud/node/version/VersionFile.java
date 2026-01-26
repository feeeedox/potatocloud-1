package net.potatocloud.node.version;

import lombok.SneakyThrows;
import net.potatocloud.api.utils.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class VersionFile {

    public static Path VERSION_FILE = PathUtils.current().resolve(".version");

    private VersionFile() {
    }

    @SneakyThrows
    public static Version read() {
        if (!Files.exists(VERSION_FILE)) {
            return null;
        }
        return Version.fromString(
                FileUtils.readFileToString(VERSION_FILE.toFile(), StandardCharsets.UTF_8).strip()
        );
    }

    @SneakyThrows
    public static void write(Version version) {
        if (!Files.exists(VERSION_FILE)) {
            Files.createFile(VERSION_FILE);

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Files.setAttribute(VERSION_FILE, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            }
        }

        FileUtils.writeStringToFile(VERSION_FILE.toFile(), version.toString(), StandardCharsets.UTF_8);
    }
}