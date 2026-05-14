package net.potatocloud.api.event.events.property;

import net.potatocloud.api.event.Event;
import net.potatocloud.api.property.Property;

public record PropertyChangedEvent(
        String holderName,
        Property<?> property,
        Object oldValue,
        Object newValue
) implements Event {}
