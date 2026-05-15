package net.potatocloud.node.group.config;

import net.potatocloud.api.property.Property;

public record PropertyConfig(
        String name,
        Object defaultValue,
        Object value
) {

    public static PropertyConfig from(Property<?> property) {
        return new PropertyConfig(
                property.getName(),
                property.getDefaultValue(),
                property.getValue()
        );
    }

    public Property<?> toProperty() {
        return Property.of(
                name,
                defaultValue,
                value
        );
    }
}