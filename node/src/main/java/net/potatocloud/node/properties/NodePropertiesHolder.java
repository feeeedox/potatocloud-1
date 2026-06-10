package net.potatocloud.node.properties;

import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyHolder;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.property.PropertyAddPacket;
import net.potatocloud.network.packet.packets.property.PropertyUpdatePacket;
import net.potatocloud.network.packet.packets.property.RequestPropertiesPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

import java.util.HashMap;
import java.util.Map;

public class NodePropertiesHolder implements PropertyHolder {

    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    private final Map<String, Property<?>> propertyMap = new HashMap<>();

    public NodePropertiesHolder(NetworkServer server, ClusterManagerImpl clusterManager) {
        this.server = server;
        this.clusterManager = clusterManager;

        server.on(RequestPropertiesPacket.class, ctx -> {
            propertyMap.values().forEach(property -> ctx.connection().send(new PropertyAddPacket(property)));
        });

        server.on(PropertyAddPacket.class, ctx -> {
            final PropertyAddPacket packet = ctx.packet();

            propertyMap.put(packet.property().name(), packet.property());

            server.broadcast().connectors().exclude(ctx.connection()).send(packet);

            if (ctx.connection().type() == ConnectionType.CONNECTOR) {
                clusterManager.broadcast(packet);
            }
        });

        server.on(PropertyUpdatePacket.class, ctx -> {
            final Property<?> property = propertyMap.get(ctx.packet().propertyName());
            if (property != null) {
                property.valueObject(ctx.packet().propertyValue());
            }

            server.broadcast().connectors().exclude(ctx.connection()).send(ctx.packet());

            if (ctx.connection().type() == ConnectionType.CONNECTOR) {
                clusterManager.broadcast(ctx.packet());
            }
        });
    }

    @Override
    public <T> void set(Property<T> key, T value, boolean fireEvent) {
        final Property<T> existing = property(key.name());
        PropertyHolder.super.set(key, value, fireEvent);

        if (existing == null) {
            final PropertyAddPacket packet = new PropertyAddPacket(key);

            server.broadcast().connectors().send(packet);
            clusterManager.broadcast(packet);
        } else {
            final PropertyUpdatePacket packet = new PropertyUpdatePacket(key.name(), value);

            server.broadcast().connectors().send(packet);
            clusterManager.broadcast(packet);
        }
    }

    @Override
    public Map<String, Property<?>> propertyMap() {
        return propertyMap;
    }

    @Override
    public String name() {
        return "Global";
    }
}
