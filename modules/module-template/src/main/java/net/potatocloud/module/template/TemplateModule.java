package net.potatocloud.module.template;

import net.potatocloud.api.module.AbstractModule;

public class TemplateModule extends AbstractModule {

    @Override
    public void onEnable() {
        info("[TemplateModule] Loaded module...");
    }

    @Override
    public void onDisable() {
        info("[TemplateModule] Unloaded module...");
    }

}
