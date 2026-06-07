package net.potatocloud.webinterface.dto.service;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.service.Service;

@Value
@Builder
public class ServiceDto {
    int serviceId;
    String name;
    String formattedUptime;
    long uptime;
    int port;
    int maxPlayers;
    int onlinePlayerCount;
    int usedMemory;

    public static ServiceDto from(Service service) {
        return ServiceDto.builder()
                .serviceId(service.getServiceId())
                .name(service.getName())
                .formattedUptime(service.getFormattedUptime())
                .uptime(service.getUptime())
                .port(service.getPort())
                .maxPlayers(service.getMaxPlayers())
                .onlinePlayerCount(service.getOnlinePlayerCount())
                .usedMemory(service.getUsedMemory())
                .build();
    }
}

