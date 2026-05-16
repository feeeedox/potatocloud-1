package net.potatocloud.node.platform.config;

public record DownloadConfig(
        String url,
        String hash,
        String parser
) {}