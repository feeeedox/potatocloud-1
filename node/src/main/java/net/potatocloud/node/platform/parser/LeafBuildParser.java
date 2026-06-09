package net.potatocloud.node.platform.parser;

import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.impl.PlatformVersionImpl;
import net.potatocloud.node.platform.BuildParser;
import net.potatocloud.node.utils.RequestUtil;
import tools.jackson.databind.JsonNode;

public class LeafBuildParser implements BuildParser {

    @Override
    public void parse(PlatformVersion version, String baseUrl) {
        try {
            String versionName = version.name();

            // Find the latest Minecraft version if the user wants the latest
            if (versionName.equalsIgnoreCase("latest")) {
                final JsonNode project = RequestUtil.request("https://api.leafmc.one/v2/projects/leaf");

                final JsonNode versionsArray = project.get("versions");

                if (versionsArray == null || versionsArray.isEmpty()) {
                    throw new RuntimeException("No versions found in Leaf API");
                }

                versionName = versionsArray
                        .get(0)
                        .asString();
            }

            // Get version info
            final JsonNode versionInfo = RequestUtil.request("https://api.leafmc.one/v2/projects/leaf/versions/" + versionName);
            final JsonNode buildsArray = versionInfo.get("builds");

            if (buildsArray == null || buildsArray.isEmpty()) {
                throw new RuntimeException("No builds found for version: " + versionName);
            }

            // Get the latest build of the chosen version
            final int latestBuildId = buildsArray.get(buildsArray.size() - 1).asInt();

            // Replace placeholders in the platform download URL
            final String downloadUrl = baseUrl
                    .replace("{version}", versionName)
                    .replace("{build}", String.valueOf(latestBuildId));

            final JsonNode latestBuild = RequestUtil
                    .request("https://api.leafmc.one/v2/projects/leaf/versions/" + versionName + "/builds/" + latestBuildId);

            final JsonNode downloads = latestBuild.get("downloads");
            final JsonNode primary = downloads != null ? downloads.get("primary") : null;

            if (primary == null) {
                throw new RuntimeException("Missing download info for Leaf build");
            }

            if (version instanceof PlatformVersionImpl versionImpl) {
                versionImpl.fileHash(primary.get("sha256").asString());
                versionImpl.downloadUrl(downloadUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Leaf build for: " + version.name(), e);
        }
    }

    @Override
    public String getName() {
        return "leaf";
    }
}