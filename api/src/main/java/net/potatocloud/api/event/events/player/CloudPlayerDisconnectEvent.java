package net.potatocloud.api.event.events.player;

import net.potatocloud.api.event.Event;

import java.util.UUID;

public record CloudPlayerDisconnectEvent(UUID playerUniqueId, String playerUsername) implements Event {}
