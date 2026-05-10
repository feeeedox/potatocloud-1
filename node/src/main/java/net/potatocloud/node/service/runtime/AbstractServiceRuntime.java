package net.potatocloud.node.service.runtime;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.node.service.ServiceImpl;

public abstract class AbstractServiceRuntime implements ServiceRuntime {

    protected String platformPluginName(ServiceImpl service) {
        final Platform platform = service.getServiceGroup().getPlatform();
        final PlatformVersion version = service.getServiceGroup().getPlatformVersion();

        if (platform.isBukkitBased()) {
            return version.isLegacy()
                    ? "potatocloud-plugin-spigot-legacy.jar"
                    : "potatocloud-plugin-spigot.jar";
        } else if (platform.isVelocityBased()) {
            return "potatocloud-plugin-velocity.jar";
        } else if (platform.isLimboBased()) {
            return "potatocloud-plugin-limbo.jar";
        } else {
            service.getLogger().error("No Plugin found for platform " + platform.getName());
            return "";
        }
    }
}
