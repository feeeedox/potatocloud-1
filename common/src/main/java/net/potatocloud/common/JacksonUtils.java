package net.potatocloud.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

public final class JacksonUtils {

    private JacksonUtils() {
    }

    public static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    public static final YAMLMapper YAML_MAPPER = YAMLMapper.builder()
            .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER)
            .enable(YAMLWriteFeature.INDENT_ARRAYS)
            .enable(YAMLWriteFeature.INDENT_ARRAYS_WITH_INDICATOR)
            .enable(YAMLWriteFeature.MINIMIZE_QUOTES)
            .enable(YAMLWriteFeature.LITERAL_BLOCK_STYLE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

}
