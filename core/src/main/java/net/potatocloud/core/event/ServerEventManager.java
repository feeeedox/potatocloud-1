package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.packets.event.EventPacket;

public class ServerEventManager extends BaseEventManager {

    private final NetworkServer server;

    public ServerEventManager(NetworkServer server) {
        this.server = server;

        server.on(EventPacket.class, ctx -> {
            final Event event = EventSerializer.deserialize(ctx.packet());
            if (event != null) {
                callLocal(event);

                server.generateBroadcast().exclude(ctx.connection()).broadcast(ctx.packet());
            }
        });
    }

    @Override
    public <T extends Event> void call(T event) {
        callLocal(event);
        server.generateBroadcast().broadcast(EventSerializer.serialize(event));
    }
}
