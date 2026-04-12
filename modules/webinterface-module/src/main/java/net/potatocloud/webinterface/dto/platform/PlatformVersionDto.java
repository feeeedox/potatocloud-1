package net.potatocloud.webinterface.dto.platform;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.platform.PlatformVersion;

@Value
@Builder
public class PlatformVersionDto {
    String name;
    String fullName;
    String downloadUrl;
    String fileHash;
    boolean local;
    boolean legacy;

    public static PlatformVersionDto from(PlatformVersion version) {
        return PlatformVersionDto.builder()
                .name(version.getName())
                .fullName(version.getFullName())
                .downloadUrl(version.getDownloadUrl())
                .fileHash(version.getFileHash())
                .local(version.isLocal())
                .legacy(version.isLegacy())
                .build();
    }
}

