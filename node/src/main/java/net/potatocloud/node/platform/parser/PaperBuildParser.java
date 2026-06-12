package net.potatocloud.node.platform.parser;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.impl.PlatformVersionImpl;
import net.potatocloud.node.platform.BuildParser;
import net.potatocloud.node.utils.RequestUtil;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PaperBuildParser implements BuildParser {

    private final String projectName;

    @Override
    public void parse(PlatformVersion version, String baseUrl) {
        try {
            String versionName = version.name();

            // Find the latest Minecraft version if the user wants the latest
            if (versionName.equalsIgnoreCase("latest")) {
                final JsonNode project = RequestUtil.request("https://fill.papermc.io/v3/projects/" + projectName);
                final JsonNode versions = project.get("versions");

                final List<String> allVersions = new ArrayList<>();

                if (versions != null && versions.isObject()) {
                    for (String key : versions.propertyNames()) {
                        final JsonNode versionArray = versions.get(key);

                        if (versionArray != null && versionArray.isArray()) {
                            for (JsonNode node : versionArray.asArray().values()) {
                                allVersions.add(node.asString());
                            }
                        }
                    }
                }

                versionName = allVersions.getFirst();
            }

            // Get the latest build of the chosen version
            final JsonNode latestBuild = RequestUtil.request("https://fill.papermc.io/v3/projects/"
                    + projectName + "/versions/" + versionName + "/builds/latest");

            final int latestBuildId = latestBuild.get("id").asInt();

            final JsonNode downloads = latestBuild.get("downloads");
            final JsonNode serverDefault = downloads != null ? downloads.get("server:default") : null;

            if (serverDefault == null) {
                throw new RuntimeException("Missing download info for Paper build");
            }

            final String sha256 = serverDefault
                    .get("checksums")
                    .get("sha256")
                    .asString();

            // Replace placeholders in the platform download URL
            final String downloadUrl = baseUrl
                    .replace("{version}", versionName)
                    .replace("{build}", String.valueOf(latestBuildId))
                    .replace("{sha256}", sha256);

            if (version instanceof PlatformVersionImpl versionImpl) {
                versionImpl.fileHash(sha256);
                versionImpl.downloadUrl(downloadUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Paper build for: " + projectName + " : " + version.name(), e);
        }
    }

    @Override
    public String getName() {
        return projectName;
    }
}
