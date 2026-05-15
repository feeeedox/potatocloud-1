package net.potatocloud.node.group.config;

import net.potatocloud.api.group.ServiceGroup;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.io.File;
import java.nio.file.Path;

public final class ServiceGroupStorage {

    private static final YAMLMapper MAPPER = YAMLMapper.builder()
            .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER)
            .enable(YAMLWriteFeature.INDENT_ARRAYS)
            .enable(YAMLWriteFeature.INDENT_ARRAYS_WITH_INDICATOR)
            .enable(YAMLWriteFeature.MINIMIZE_QUOTES)
            .enable(YAMLWriteFeature.LITERAL_BLOCK_STYLE)
            .build();

    private ServiceGroupStorage() {
    }

    public static void save(ServiceGroup group, Path directory) {
        final Path path = directory.resolve(group.getName() + ".yml");

        MAPPER.writeValue(path, ServiceGroupConfig.from(group));
    }

    public static ServiceGroup load(File file) {
        return MAPPER.readValue(file, ServiceGroupConfig.class).toGroup();
    }
}