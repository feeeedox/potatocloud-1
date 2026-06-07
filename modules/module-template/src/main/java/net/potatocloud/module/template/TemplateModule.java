package net.potatocloud.module.template;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.module.AbstractModule;

public class TemplateModule extends AbstractModule {

    @Override
    public void onEnable() {
        CloudAPI.instance().logger().info("[TemplateModule] Loaded module...");
    }

    @Override
    public void onDisable() {
        CloudAPI.instance().logger().info("[TemplateModule] Unloaded module...");
    }

}
