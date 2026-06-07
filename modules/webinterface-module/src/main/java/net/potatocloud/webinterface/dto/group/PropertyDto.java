package net.potatocloud.webinterface.dto.group;

import lombok.Builder;
import net.potatocloud.api.property.Property;

@Builder
public record PropertyDto(String name, Object value, Object defaultValue) {
    public static PropertyDto from(Property<?> property) {
        return PropertyDto.builder()
                .name(property.getName())
                .value(property.getValue())
                .defaultValue(property.getDefaultValue())
                .build();
    }
}

