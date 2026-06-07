package net.potatocloud.webinterface.dto.stats;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class JoinStatsDto {
    int total;
    List<JoinPointDto> data;
}

