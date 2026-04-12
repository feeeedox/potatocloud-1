package net.potatocloud.webinterface.dto.stats;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StatsDto {
    long uptime;
    int groups;
    int services;
    int onlinePlayers;
}

