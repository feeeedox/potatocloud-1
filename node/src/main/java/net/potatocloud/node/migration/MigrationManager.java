package net.potatocloud.node.migration;

import net.potatocloud.api.version.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {

    private final Version previousVersion;
    private final List<Migration> migrations = new ArrayList<>();

    public MigrationManager(Version previousVersion) {
        this.previousVersion = previousVersion;
    }

    public void registerMigration(Migration migration) {
        migrations.add(migration);

        migrations.sort(Comparator.comparing(Migration::from));
    }

    public void migrate() {
        Version versionToMigrate = previousVersion;

        while (true) {
            final Migration next = findNextMigration(versionToMigrate);
            if (next == null) {
                break;
            }

            next.execute();

            versionToMigrate = next.to();
        }
    }

    private Migration findNextMigration(Version current) {
        return migrations.stream()
                .filter(m -> m.from().equals(current))
                .findFirst()
                .orElse(null);
    }
}
