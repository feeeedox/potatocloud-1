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

            propertyMap.put(packet.property().getName(), packet.property());

            server.broadcast().connectors().exclude(ctx.connection()).send(packet);

            if (ctx.connection().type() == ConnectionType.CONNECTOR) {
                clusterManager.broadcast(packet);
            }
        });

        server.on(PropertyUpdatePacket.class, ctx -> {
            final Property<?> property = propertyMap.get(ctx.packet().propertyName());
            if (property != null) {
                property.setValueObject(ctx.packet().propertyValue());
            }

            server.broadcast().connectors().exclude(ctx.connection()).send(ctx.packet());

            if (ctx.connection().type() == ConnectionType.CONNECTOR) {
                clusterManager.broadcast(ctx.packet());
            }
        });
    }

    @Override
    public <T> void setProperty(Property<T> property, T value, boolean fireEvent) {
        final Property<T> existing = getProperty(property.getName());
        PropertyHolder.super.setProperty(property, value, fireEvent);

        if (existing == null) {
            final PropertyAddPacket packet = new PropertyAddPacket(property);

            server.broadcast().connectors().send(packet);
            clusterManager.broadcast(packet);
        } else {
            final PropertyUpdatePacket packet = new PropertyUpdatePacket(property.getName(), value);

            server.broadcast().connectors().send(packet);
            clusterManager.broadcast(packet);
        }
    }

    @Override
    public Map<String, Property<?>> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public String getPropertyHolderName() {
        return "Global";
    }
}
