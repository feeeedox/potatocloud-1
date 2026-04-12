package net.potatocloud.webinterface.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleyaml.configuration.implementation.snakeyaml.lib.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebInterfaceConfig {

    private int port;
    private String bindAddress;
    private String jwtSecret;
    private boolean requireAuth;
    private List<String> apiKeys;
    private int wsPingIntervalSeconds;
    private int wsUpdateIntervalSeconds;

    @SuppressWarnings("unchecked")
    public static WebInterfaceConfig load() {
        Path configDir = Path.of("modules", "webinterface");
        Path configFile = configDir.resolve("config.yml");

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            if (!Files.exists(configFile)) {
                writeDefaults(configFile);
            }

            try (InputStream in = Files.newInputStream(configFile)) {
                Map<String, Object> data = new Yaml().load(in);
                return WebInterfaceConfig.builder()
                        .port((int) data.getOrDefault("port", 8080))
                        .bindAddress((String) data.getOrDefault("bind-address", "127.0.0.1"))
                        .jwtSecret((String) data.getOrDefault("jwt-secret", "a-very-long-secret-key-with-a-minimum-of-32-characters"))
                        .requireAuth((boolean) data.getOrDefault("require-auth", true))
                        .apiKeys((List<String>) data.getOrDefault("api-keys", new ArrayList<>()))
                        .wsPingIntervalSeconds((int) data.getOrDefault("ws-ping-interval-seconds", 30))
                        .wsUpdateIntervalSeconds((int) data.getOrDefault("ws-update-interval-seconds", 5))
                        .build();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load webinterface config", exception);
        }
    }

    private static void writeDefaults(Path file) throws IOException {
        String generatedKey = "change-me-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Files.writeString(file, String.join("\n",
                "# PotatoCloud WebInterface Module",
                "",
                "port: 8080",
                "# Use 127.0.0.1 to expose only locally, 0.0.0.0 for network access (not recommended for production without proper security measures!)",
                "bind-address: \"127.0.0.1\"",
                "# The JWT secret is used for the websocket validation",
                "jwt-secret: a-very-long-secret-key-with-a-minimum-of-32-characters",
                "",
                "# If you disable this, the websocket and REST api can be accessed WITHOUT any authentication! This is NOT recommended for production environments.",
                "require-auth: true",
                "# These api keys are used for the REST api (They are not used for the websocket authentication, which relies solely on the JWT token)",
                "api-keys:",
                "  - \"" + generatedKey + "\"",
                "",
                "ws-ping-interval-seconds: 30",
                "ws-update-interval-seconds: 5"
        ));
    }
}

