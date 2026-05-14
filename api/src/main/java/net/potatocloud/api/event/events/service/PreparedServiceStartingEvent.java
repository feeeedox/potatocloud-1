package net.potatocloud.api.event.events.service;

import net.potatocloud.api.event.Event;

public record PreparedServiceStartingEvent(String serviceName) implements Event {}
