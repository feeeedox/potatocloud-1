package net.potatocloud.connector.platform;

import lombok.Getter;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformManager;
import net.potatocloud.api.platform.impl.PlatformImpl;
import net.potatocloud.network.NetworkClient;
import net.potatocloud.network.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.network.packet.packets.platform.PlatformRemovePacket;
import net.potatocloud.network.packet.packets.platform.PlatformUpdatePacket;
import net.potatocloud.network.packet.packets.platform.RequestPlatformsPacket;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlatformManagerImpl implements PlatformManager {

    private final NetworkClient client;
    private final List<Platform> platforms = new ArrayList<>();

    public PlatformManagerImpl(NetworkClient client) {
        this.client = client;

        // Since this class is very short just keep the package listeners here as long as there are not too many and they are not too big
        client.on(PlatformAddPacket.class, ctx -> {
            platforms.add(ctx.packet().platform());
        });

        client.on(PlatformRemovePacket.class, ctx -> {
            platforms.remove(getPlatform(ctx.packet().platformName()));
        });

        client.on(PlatformUpdatePacket.class, ctx -> {
            final Platform platform = getPlatform(ctx.packet().platform().getName());
            if (platform == null) {
                return;
            }
            platform.setVersions(ctx.packet().platform().getVersions());
        });

        client.send(new RequestPlatformsPacket());
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

        platforms.add(platform);

        client.send(new PlatformAddPacket(platform));

        return platform;
    }

    @Override
    public void updatePlatform(Platform platform) {
        client.send(new PlatformUpdatePacket(platform));
    }
}