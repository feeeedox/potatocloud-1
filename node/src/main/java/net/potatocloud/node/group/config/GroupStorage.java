package net.potatocloud.node.group.config;

import net.potatocloud.api.group.Group;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.nio.file.Files;
import java.nio.file.Path;

public final class GroupStorage {

    private static final YAMLMapper MAPPER = YAMLMapper.builder()
            .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER)
            .enable(YAMLWriteFeature.INDENT_ARRAYS)
            .enable(YAMLWriteFeature.INDENT_ARRAYS_WITH_INDICATOR)
            .enable(YAMLWriteFeature.MINIMIZE_QUOTES)
            .enable(YAMLWriteFeature.LITERAL_BLOCK_STYLE)
            .build();

    private GroupStorage() {
    }

    public static void save(Group group, Path directory) {
        try {
            final Path path = directory.resolve(group.name() + ".yml");
            Files.createDirectories(path.getParent());

            MAPPER.writeValue(path, GroupConfig.from(group));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save group: " + group.name(), e);
        }
    }

    public static Group load(Path path) {
        return MAPPER.readValue(path, GroupConfig.class).toGroup();
    }
}