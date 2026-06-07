package net.potatocloud.webinterface.dto.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDto {
    String error;
}

