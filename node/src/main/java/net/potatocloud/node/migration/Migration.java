package net.potatocloud.node.migration;

import net.potatocloud.api.version.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Migration {

    private static final Path BACKUPS_DIRECTORY = Path.of("backups");

    private final String name;
    private final Version from;
    private final Version to;

    public Migration(String name, Version from, Version to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public String name() {
        return name;
    }

    public Version from() {
        return from;
    }

    public Version to() {
        return to;
    }

    public abstract void execute();

    protected Path createBackupsDirectory(String subDirectoryName) {
        try {
            final Path subDirectory = BACKUPS_DIRECTORY.resolve(subDirectoryName);
            if (!Files.exists(subDirectory)) {
                Files.createDirectories(subDirectory);
            }

            final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            final Path backupsDirectory = subDirectory.resolve(timestamp);
            Files.createDirectories(backupsDirectory);

            return backupsDirectory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create backups directory!", e);
        }
    }
}
