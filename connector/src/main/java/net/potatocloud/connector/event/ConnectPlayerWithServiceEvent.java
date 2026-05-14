package net.potatocloud.connector.event;

import net.potatocloud.api.event.Event;

public record ConnectPlayerWithServiceEvent(String playerUsername, String serviceName) implements Event {}
