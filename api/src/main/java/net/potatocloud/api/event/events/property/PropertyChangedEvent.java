package net.potatocloud.api.event.events.property;

import net.potatocloud.api.event.Event;

public record PropertyChangedEvent(
        String holderName,
        String propertyName,
        Object oldValue,
        Object newValue
) implements Event {}
