package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.EventHandler;
import net.potatocloud.api.event.PublishTarget;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packet.packets.event.EventPacket;

public final class ClientEventBus implements EventBus {

    private final DefaultEventBus local = new DefaultEventBus();
    private final NetworkClient client;

    public ClientEventBus(NetworkClient client) {
        this.client = client;

        client.on(EventPacket.class, ctx -> local.publish(JsonEventCodec.decode(ctx.packet())));
    }

    @Override
    public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        local.subscribe(eventType, handler);
    }

    @Override
    public <T extends Event> void publish(T event) {
        publish(event, PublishTarget.BOTH);
    }

    @Override
    public <T extends Event> void publish(T event, PublishTarget target) {
        switch (target) {
            case LOCAL -> local.publish(event);
            case NETWORK -> client.send(JsonEventCodec.encode(event));
            case BOTH -> {
                local.publish(event);
                client.send(JsonEventCodec.encode(event));
            }
        }
    }
}