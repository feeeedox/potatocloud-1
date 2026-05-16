package net.potatocloud.node.platform.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.potatocloud.api.platform.Platform;

import java.util.Collections;
import java.util.List;

public record PlatformConfig(
        String base,
        DownloadConfig download,
        ProcessingConfig processing,
        FlagsConfig flags,
        @JsonProperty("prepare-steps")
        List<String> prepareSteps,
        List<VersionConfig> versions
) {

    public PlatformConfig {
        prepareSteps = prepareSteps != null ? prepareSteps : Collections.emptyList();
        versions = versions != null ? versions : Collections.emptyList();
    }

    public static PlatformConfig from(Platform platform) {
        return new PlatformConfig(
                platform.getBase(),
                new DownloadConfig(
                        platform.getDownloadUrl(),
                        platform.getHashType(),
                        platform.getParser()
                ),
                new ProcessingConfig(
                        platform.getPreCacheBuilder()
                ),
                new FlagsConfig(
                        platform.isCustom(),
                        platform.isProxy()
                ),
                platform.getPrepareSteps(),
                platform.getVersions()
                        .stream()
                        .map(version -> new VersionConfig(
                                version.getName(),
                                version.getDownloadUrl(),
                                version.isLegacy(),
                                version.isLocal()
                        ))
                        .toList()
        );
    }
}