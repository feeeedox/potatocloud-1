package net.potatocloud.node.platform;

import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformManager;
import net.potatocloud.api.platform.impl.PlatformImpl;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.network.packet.packets.platform.PlatformUpdatePacket;
import net.potatocloud.network.packet.packets.platform.RequestPlatformsPacket;
import net.potatocloud.node.platform.listeners.PlatformAddListener;
import net.potatocloud.node.platform.listeners.PlatformUpdateListener;
import net.potatocloud.node.platform.listeners.RequestPlatformsListener;

import java.util.Collections;
import java.util.List;

public class PlatformManagerImpl implements PlatformManager {

    private final Logger logger;
    private final NetworkServer server;
    private final PlatformFileHandler fileHandler;
    private final List<Platform> platforms;

    public PlatformManagerImpl(Logger logger, NetworkServer server) {
        this.logger = logger;
        this.server = server;
        this.fileHandler = new PlatformFileHandler(logger);
        this.platforms = fileHandler.loadPlatformsFile();

        server.on(RequestPlatformsPacket.class, new RequestPlatformsListener(this));
        server.on(PlatformUpdatePacket.class, new PlatformUpdateListener(this));
        server.on(PlatformAddPacket.class, new PlatformAddListener(this));
    }

    public List<Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    @Override
    public Platform createPlatform(String name, String downloadUrl, boolean custom, boolean isProxy, String base, String preCacheBuilder, String parser, String hashType, List<String> prepareSteps) {
        final Platform platform = new PlatformImpl(
                name,
                downloadUrl,
                custom,
                isProxy,
                base,
                preCacheBuilder,
                parser,
                hashType,
                prepareSteps
        );

        addPlatform(platform);

        server.broadcast().connectors().send(new PlatformAddPacket(platform));

        logger.info("Platform &a" + name + " &7was successfully created");

        return platform;
    }

    @Override
    public void updatePlatform(Platform platform) {
        server.broadcast().connectors().send(new PlatformUpdatePacket(platform));

        fileHandler.savePlatform(platform);
    }

    public void addPlatform(Platform platform) {
        if (platform == null || exists(platform.name())) {
            return;
        }

        platforms.add(platform);

        fileHandler.savePlatform(platform);
    }
}