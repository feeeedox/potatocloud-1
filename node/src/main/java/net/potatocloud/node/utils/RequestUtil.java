package net.potatocloud.node.utils;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.common.JacksonUtils;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class RequestUtil {

    private RequestUtil() {
    }

    public static JsonNode request(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpRequest buildRequest = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", "potatocloud/" + CloudAPI.VERSION + " (https://github.com/potatocloudmc/potatocloud)")
                    .build();

            final HttpResponse<String> buildResponse = client.send(buildRequest, HttpResponse.BodyHandlers.ofString());

            return JacksonUtils.JSON_MAPPER.readTree(buildResponse.body());
        } catch (Exception e) {
            throw new RuntimeException("Failed to request: " + url, e);
        }
    }
}
