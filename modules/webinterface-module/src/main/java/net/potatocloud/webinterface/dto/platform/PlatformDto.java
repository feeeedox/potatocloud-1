package net.potatocloud.webinterface.dto.platform;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.platform.Platform;

import java.util.List;

@Value
@Builder
public class PlatformDto {
    String name;
    String base;
    String downloadUrl;
    boolean custom;
    boolean proxy;
    boolean bukkitBased;
    boolean paperBased;
    boolean velocityBased;
    boolean limboBased;
    List<PlatformVersionDto> versions;
    List<String> prepareSteps;

    public static PlatformDto from(Platform platform) {
        return PlatformDto.builder()
                .name(platform.getName())
                .base(platform.getBase())
                .downloadUrl(platform.getDownloadUrl())
                .custom(platform.isCustom())
                .proxy(platform.isProxy())
                .bukkitBased(platform.isBukkitBased())
                .paperBased(platform.isPaperBased())
                .velocityBased(platform.isVelocityBased())
                .limboBased(platform.isLimboBased())
                .versions(platform.getVersions().stream().map(PlatformVersionDto::from).toList())
                .prepareSteps(platform.getPrepareSteps())
                .build();
    }
}

