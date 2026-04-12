package net.potatocloud.webinterface.dto.player;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlayerCountDto {
    int online;
}

