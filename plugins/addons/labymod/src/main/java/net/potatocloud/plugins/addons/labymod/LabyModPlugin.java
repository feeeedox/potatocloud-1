package net.potatocloud.plugins.addons.labymod;

import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import net.potatocloud.plugins.addons.labymod.listener.LabyModPlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class LabyModPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final Config config = new Config("plugins/potatocloud-labymod", "config.yml");
        config.load();
        LabyModProtocolService.initialize(this);

        getServer().getPluginManager().registerEvents(new LabyModPlayerJoinListener(config), this);
    }
}
