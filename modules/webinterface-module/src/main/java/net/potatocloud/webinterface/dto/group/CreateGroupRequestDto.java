package net.potatocloud.webinterface.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequestDto {
    private String name;
    private String platform;
    private String platformVersion;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayerCount;
    private int maxMemory;
    private boolean fallback;
    private boolean isStatic;
    private boolean useModernVelocityForwarding;
    private int startPriority;
    private int newServicePercent;
    private String startCommand;
    private List<String> customJvmFlags;
    private List<PropertyDto> properties;
}

