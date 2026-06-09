package net.potatocloud.api.module;
import net.potatocloud.api.version.Version;

public abstract class AbstractModule implements Module {

    private String name;
    private Version version;

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Version version() {
        return version;
    }

    public void version(Version version) {
        this.version = version;
    }
}
