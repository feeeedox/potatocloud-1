package net.potatocloud.connector.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.api.service.impl.ServiceImpl;
import java.util.Map;

public class ConnectorService extends ServiceImpl {

    public ConnectorService(
            String name,
            int serviceId,
            int port,
            long startTimestamp,
            String groupName,
            Map<String, Property<?>> propertyMap,
            ServiceStatus status,
            int maxPlayers,
            int usedMemory
    ) {
        super(serviceId, port, name, CloudAPI.getInstance().getServiceGroupManager().getServiceGroup(groupName), propertyMap, startTimestamp, status, maxPlayers, usedMemory);
    }
}