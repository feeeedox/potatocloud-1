package net.potatocloud.connector.logging;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.NetworkClient;
import net.potatocloud.network.packet.packets.logging.LogMessagePacket;

@RequiredArgsConstructor
public class ConnectorLogger implements Logger {

    private final NetworkClient client;

    @Override
    public void log(Level level, String message) {
        client.send(new LogMessagePacket(level.name(), message));
    }
}
