package net.potatocloud.node.group.config;

import net.potatocloud.api.property.Property;

public record PropertyConfig(
        String name,
        Object defaultValue,
        Object value
) {

    public static PropertyConfig from(Property<?> property) {
        return new PropertyConfig(
                property.name(),
                property.defaultValue(),
                property.value()
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