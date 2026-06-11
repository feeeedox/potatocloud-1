package net.potatocloud.api.player.impl;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CloudPlayerImpl implements CloudPlayer {

    private final String username;
    private final UUID uniqueId;
    private String proxyName;
    private String serviceName;
    private final Map<String, Property<?>> propertyMap;

    public CloudPlayerImpl(String username, UUID uniqueId, String proxyName) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.proxyName = proxyName;
        this.propertyMap = new HashMap<>();
    }

    public CloudPlayerImpl(String username, UUID uniqueId, String proxyName, String serviceName, Map<String, Property<?>> propertyMap) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.proxyName = proxyName;
        this.serviceName = serviceName;
        this.propertyMap = propertyMap;
    }

    @Override
    public UUID uniqueId() {
        return uniqueId;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public Service proxy() {
        return CloudAPI.instance().serviceManager().find(proxyName)
                .orElseThrow(() -> new IllegalStateException("Proxy not found for player: " + proxyName + ", " + username));
    }

    public void proxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    @Override
    public Optional<Service> service() {
        return CloudAPI.instance().serviceManager().find(serviceName);
    }

    public void serviceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String name() {
        return username();
    }

    @Override
    public Map<String, Property<?>> propertyMap() {
        return propertyMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudPlayerImpl other)) {
            return false;
        }
        return uniqueId.equals(other.uniqueId);
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}
