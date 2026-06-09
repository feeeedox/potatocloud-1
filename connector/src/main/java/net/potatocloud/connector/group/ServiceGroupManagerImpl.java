package net.potatocloud.connector.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.property.Property;
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
        if (group == null || existsServiceGroup(group.name())) {
            return;
        }
        groups.add(group);
    }

    @Override
    public ServiceGroup getServiceGroup(String name) {
        return groups.stream()
                .filter(group -> group.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return Collections.unmodifiableList(groups);
    }

    @Override
    public void createServiceGroup(String name, String nodeName, String platformName, String platformVersionName, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic, int startPriority, int startPercentage, String javaCommand, List<String> customJvmFlags, Map<String, Property<?>> propertyMap) {
        if (existsServiceGroup(name)) {
            return;
        }

        final ServiceGroupImpl group = new ServiceGroupImpl(
                name,
                nodeName,
                platformName,
                platformVersionName,
                javaCommand,
                Set.copyOf(customJvmFlags), // todo temporary
                maxPlayers,
                maxMemory,
                minOnlineCount,
                maxOnlineCount,
                isStatic,
                fallback,
                startPriority,
                startPercentage,
                propertyMap
        );

        client.send(new GroupAddPacket(group));

        addServiceGroup(group);
    }

    @Override
    public void deleteServiceGroup(String name) {
        client.send(new GroupDeletePacket(name));

        deleteServiceGroupLocal(name);
    }

    public void deleteServiceGroupLocal(String name) {
        final ServiceGroup group = getServiceGroup(name);
        if (group == null) {
            return;
        }
        groups.remove(group);
    }

    @Override
    public void updateServiceGroup(ServiceGroup group) {
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

    @Override
    public boolean existsServiceGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        return groups.stream().anyMatch(group -> group != null && group.name().equalsIgnoreCase(groupName));
    }
}
