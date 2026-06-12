package net.potatocloud.api.event.events.service;

import net.potatocloud.api.event.Event;

public record ServiceStartingEvent(String serviceName) implements Event {}
