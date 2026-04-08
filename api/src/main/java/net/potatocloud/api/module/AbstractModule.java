package net.potatocloud.api.module;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.utils.version.Version;

@Setter
@Getter
public abstract class AbstractModule implements PotatoModule {

    @Getter
    protected ModuleLogger logger;
    @Getter
    private String name;
    @Getter
    private Version version;

    protected void info(String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }

    protected void warn(String message) {
        if (logger != null) {
            logger.warn(message);
        } else {
            System.out.println("[WARN] " + message);
        }
    }

    protected void error(String message) {
        if (logger != null) {
            logger.error(message);
        } else {
            System.out.println("[ERROR] " + message);
        }
    }

    protected CloudAPI getCloudAPI() {
        return CloudAPI.getInstance();
    }
}