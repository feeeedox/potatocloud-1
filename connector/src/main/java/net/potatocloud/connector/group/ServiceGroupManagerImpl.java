package net.potatocloud.connector.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.connector.group.listeners.GroupAddListener;
import net.potatocloud.connector.group.listeners.GroupDeleteListener;
import net.potatocloud.connector.group.listeners.GroupUpdateListener;
import net.potatocloud.network.NetworkClient;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;
import net.potatocloud.network.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.network.packet.packets.group.RequestGroupsPacket;

import java.util.*;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> groups = new ArrayList<>();
    private final NetworkClient client;

    public ServiceGroupManagerImpl(NetworkClient client) {
        this.client = client;

        client.on(GroupAddPacket.class, new GroupAddListener(this));
        client.on(GroupDeletePacket.class, new GroupDeleteListener(this));
        client.on(GroupUpdatePacket.class, new GroupUpdateListener(this));

        client.send(new RequestGroupsPacket());
    }

    public void addServiceGroup(ServiceGroup group) {
        if (group == null || exists(group.name())) {
            return;
        }
        groups.add(group);
    }

    @Override
    public Optional<ServiceGroup> find(String name) {
        return groups.stream()
                .filter(group -> group.name().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<ServiceGroup> groups() {
        return Collections.unmodifiableList(groups);
    }

    @Override
    public void create(ServiceGroup group) {
        if (exists(group.name())) {
            return;
        }

        client.send(new GroupAddPacket(group));
        addServiceGroup(group);
    }

    @Override
    public void delete(ServiceGroup group) {
        client.send(new GroupDeletePacket(group.name()));
        deleteLocal(group.name());
    }

    public void deleteLocal(String name) {
        find(name).ifPresent(groups::remove);
    }

    @Override
    public void update(ServiceGroup group) {
        client.send(new GroupUpdatePacket(
                group.name(),
                group.customJvmFlags(),
                group.maxPlayers(),
                group.maxMemory(),
                group.minServices(),
                group.maxServices(),
                group.fallback(),
                group.startPriority(),
                group.startPercentage(),
                group.templates(),
                group.getPropertyMap()
        ));
    }
}
