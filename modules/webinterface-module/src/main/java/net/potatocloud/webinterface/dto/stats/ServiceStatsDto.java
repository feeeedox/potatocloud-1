package net.potatocloud.webinterface.dto.stats;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceStatsDto {
    int running;
    int starting;
    int stopping;
    int currentMemoryUsage;
}