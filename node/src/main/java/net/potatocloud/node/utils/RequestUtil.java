package net.potatocloud.node.utils;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.common.JacksonUtils;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class RequestUtil {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String USER_AGENT =
            "potatocloud/" + CloudAPI.VERSION + " (https://github.com/potatocloudmc/potatocloud)";

    private RequestUtil() {
    }

    public static JsonNode request(String url) {
        try {
            final HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofSeconds(10))
                    .build();

            final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            return JacksonUtils.JSON_MAPPER.readTree(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Failed to request: " + url, e);
        }
    }
}
