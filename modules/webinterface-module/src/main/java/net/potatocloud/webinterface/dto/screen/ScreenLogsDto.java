package net.potatocloud.webinterface.dto.screen;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ScreenLogsDto {
    String screen;
    List<String> logs;
}

