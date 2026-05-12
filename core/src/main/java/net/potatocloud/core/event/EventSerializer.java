package net.potatocloud.core.event;

import com.google.gson.Gson;
import net.potatocloud.api.event.Event;
import net.potatocloud.core.networking.packet.packets.event.EventPacket;

public class EventSerializer {

    private static final Gson gson = new Gson();

    public static EventPacket serialize(Event event) {
        return new EventPacket(event.getClass().getName(), gson.toJson(event));
    }

    public static Event deserialize(EventPacket packet) {
        try {
            final Class<?> clazz = Class.forName(packet.eventClass());
            return (Event) gson.fromJson(packet.eventJson(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event: " + packet.eventClass(), e);
        }
    }
}
