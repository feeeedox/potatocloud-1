package net.potatocloud.node.properties;

import lombok.Getter;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyHolder;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.property.PropertyAddPacket;
import net.potatocloud.network.packet.packets.property.PropertyUpdatePacket;
import net.potatocloud.network.packet.packets.property.RequestPropertiesPacket;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NodePropertiesHolder implements PropertyHolder {

    private final NetworkServer server;

    private final Map<String, Property<?>> propertyMap = new HashMap<>();

    public NodePropertiesHolder(NetworkServer server) {
        this.server = server;

        server.on(RequestPropertiesPacket.class, ctx -> {
            propertyMap.values().forEach(property -> ctx.connection().send(new PropertyAddPacket(property)));
        });

        server.on(PropertyAddPacket.class, ctx -> {
            final PropertyAddPacket packet = ctx.packet();

            propertyMap.put(packet.property().getName(), packet.property());

            // Add the property on all other connectors as well
            server.generateBroadcast().exclude(ctx.connection()).broadcast(packet);
        });

        server.on(PropertyUpdatePacket.class, ctx -> {
            final Property<?> property = propertyMap.get(ctx.packet().propertyName());
            if (property != null) {
                property.setValueObject(ctx.packet().propertyValue());
            }

            // Update the property on all other connectors as well
            server.generateBroadcast().exclude(ctx.connection()).broadcast(ctx.packet());
        });
    }

    @Override
    public <T> void setProperty(Property<T> property, T value, boolean fireEvent) {
        final Property<T> existing = getProperty(property.getName());
        PropertyHolder.super.setProperty(property, value, fireEvent);

        if (existing == null) {
            // Property was just created, so send the add packet to the connector
            server.generateBroadcast().broadcast(new PropertyAddPacket(property));
        } else {
            // Property was just updated, so send the update packet to the connector
            server.generateBroadcast().broadcast(new PropertyUpdatePacket(property.getName(), value));
        }
    }

    @Override
    public String getPropertyHolderName() {
        return "Global";
    }
}
