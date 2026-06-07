package net.potatocloud.api.service.impl;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final String host;
    private final String name;
    private final String groupName;
    private final Map<String, Property<?>> propertyMap;

    private long startTimestamp;
    private ServiceState status;
    private int maxPlayers;
    private int usedMemory;

    public ServiceImpl(int serviceId, String host, int port, String name, String groupName, Map<String, Property<?>> propertyMap, long startTimestamp, ServiceState status, int maxPlayers, int usedMemory) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.name = name;
        this.groupName = groupName;
        this.propertyMap = propertyMap;
        this.startTimestamp = startTimestamp;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.usedMemory = usedMemory;
    }

    @Override
    public ServiceGroup group() {
        return CloudAPI.instance().groupManager().getServiceGroup(groupName);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int id() {
        return serviceId;
    }

    @Override
    public Optional<ClusterNode> node() {
        return group().node();
    }

    @Override
    public ServiceState state() {
        return status;
    }

    @Override
    public void state(ServiceState status) {
        this.status = status;
    }

    @Override
    public Instant startedAt() {
        return Instant.ofEpochMilli(startTimestamp);
    }

    @Override
    public Duration uptime() {
        return Duration.ofMillis(System.currentTimeMillis() - startTimestamp);
    }

    protected void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public int maxPlayers() {
        return maxPlayers;
    }

    @Override
    public void maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int usedMemory() {
        return usedMemory;
    }

    public void usedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Map<String, Property<?>> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public String getPropertyHolderName() {
        return name;
    }
}
