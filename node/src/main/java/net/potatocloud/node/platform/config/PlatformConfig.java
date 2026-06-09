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
                platform.base(),
                new DownloadConfig(
                        platform.downloadUrl(),
                        platform.hashType(),
                        platform.parser()
                ),
                new ProcessingConfig(
                        platform.preCacheBuilder()
                ),
                new FlagsConfig(
                        platform.custom(),
                        platform.proxy()
                ),
                platform.prepareSteps(),
                platform.versions()
                        .stream()
                        .map(version -> new VersionConfig(
                                version.name(),
                                version.downloadUrl(),
                                version.legacy(),
                                version.local()
                        ))
                        .toList()
        );
    }
}