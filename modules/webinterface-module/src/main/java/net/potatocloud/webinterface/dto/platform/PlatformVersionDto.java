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
                .name(version.name())
                .fullName(version.fullName())
                .downloadUrl(version.downloadUrl())
                .fileHash(version.fileHash())
                .local(version.local())
                .legacy(version.legacy())
                .build();
    }
}

