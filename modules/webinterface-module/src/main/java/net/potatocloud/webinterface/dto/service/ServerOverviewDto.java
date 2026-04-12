package net.potatocloud.webinterface.dto.service;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.service.Service;

@Value
@Builder
public class ServerOverviewDto {

    String service;
    String serviceStatus;
    int playerCount;
    int maxPlayerCount;
    long uptime;

    public static ServerOverviewDto from(Service service) {
        return ServerOverviewDto.builder()
                .service(service.getName())
                .serviceStatus(service.getStatus().name())
                .playerCount(service.getOnlinePlayerCount())
                .maxPlayerCount(service.getMaxPlayers())
                .uptime(service.getStartTimestamp())
                .build();
    }

}
