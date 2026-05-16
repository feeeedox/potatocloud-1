package net.potatocloud.common.config;

public interface Config {

    void load();

    void save();

    void reload();

    ConfigNode get(String path);

}
