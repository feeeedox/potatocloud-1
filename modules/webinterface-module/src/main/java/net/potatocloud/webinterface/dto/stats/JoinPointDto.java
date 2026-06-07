package net.potatocloud.webinterface.dto.stats;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JoinPointDto {
    String hour;
    int joins;
}

