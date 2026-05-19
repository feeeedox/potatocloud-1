package net.potatocloud.api.module;

import net.potatocloud.api.version.Version;

public interface Module {

    void onLoad();

    void onEnable();

    void onDisable();

    String getName();

    Version getVersion();

}
