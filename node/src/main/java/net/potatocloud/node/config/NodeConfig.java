package net.potatocloud.node.config;

import lombok.Getter;
import net.potatocloud.common.ResourceFileUtils;
import net.potatocloud.common.config.Config;
import net.potatocloud.common.config.yaml.YamlConfig;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class NodeConfig {

    public static final String CONFIG_FILE_NAME = "config.yml";

    private Config config;

    private String prompt;
    private boolean enableBanner;
    private int primaryColorCode;
    private boolean logPlayerConnections;

    private int serviceStartPort;
    private int proxyStartPort;
    private String splitter;
    private boolean platformAutoUpdate;
    private int maxServices;
    private int maxStartingServices;
    private int killTimeout;
    private boolean memoryCheckEnabled;
    private int maxMemory;

    private String groupsFolder;
    private String staticFolder;
    private String tempServicesFolder;
    private String templatesFolder;
    private String platformsFolder;
    private String modulesFolder;
    private String logsFolder;
    private String dataFolder;
    private String backupsFolder;

    private String nodeHost;
    private int nodePort;

    private boolean disableUpdateChecker;
    private boolean debug;

    public void load(Path path) {
        this.config = new YamlConfig(path.getParent().getFileName().toString(), path.getFileName().toString());
        config.load();

        final Path configPath = Path.of(CONFIG_FILE_NAME);

        if (!Files.exists(configPath)) {
            ResourceFileUtils.copyResourceFile(
                    CONFIG_FILE_NAME,
                    configPath
            );
        }

        prompt = config.getString("console.prompt");
        enableBanner = config.getBoolean("console.enable-banner");
        primaryColorCode = config.getInt("console.primary-color");
        logPlayerConnections = config.getBoolean("console.log-player-connections");

        serviceStartPort = config.getInt("service.service-start-port");
        proxyStartPort = config.getInt("service.proxy-start-port");
        splitter = config.getString("service.splitter");
        platformAutoUpdate = config.getBoolean("service.auto-update-platforms");
        maxServices = config.getInt("service.max-services");
        maxStartingServices = config.getInt("service.max-starting-services");
        killTimeout = config.getInt("service.kill-timeout");
        memoryCheckEnabled = config.getBoolean("service.memory-check-enabled");
        maxMemory = config.getInt("service.max-memory");

        groupsFolder = config.getString("folders.groups");
        staticFolder = config.getString("folders.static");
        tempServicesFolder = config.getString("folders.temp-services");
        templatesFolder = config.getString("folders.templates");
        platformsFolder = config.getString("folders.platforms");
        modulesFolder = config.getString("folders.modules");
        logsFolder = config.getString("folders.logs");
        dataFolder = config.getString("folders.data");
        backupsFolder = config.getString("folders.backups");

        nodeHost = config.getString("node.host");
        nodePort = config.getInt("node.port");

        disableUpdateChecker = config.getBoolean("disable-update-checker");
        debug = config.getBoolean("debug");
    }
}
