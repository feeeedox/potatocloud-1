package net.potatocloud.api.service.impl;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;

import java.util.Map;

public abstract class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final String name;
    private final ServiceGroup group;
    private final Map<String, Property<?>> propertyMap;

    private long startTimestamp;
    private ServiceStatus status;
    private int maxPlayers;
    private int usedMemory;

    public ServiceImpl(int serviceId, int port, String name, ServiceGroup group, Map<String, Property<?>> propertyMap, long startTimestamp, ServiceStatus status, int maxPlayers, int usedMemory) {
        this.serviceId = serviceId;
        this.port = port;
        this.name = name;
        this.group = group;
        this.propertyMap = propertyMap;
        this.startTimestamp = startTimestamp;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.usedMemory = usedMemory;
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getServiceId() {
        return serviceId;
    }

    @Override
    public ServiceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    protected void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public int getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    @Override
    public int getPort() {
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
