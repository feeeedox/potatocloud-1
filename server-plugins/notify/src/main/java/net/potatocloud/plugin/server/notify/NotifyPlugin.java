package net.potatocloud.plugin.server.notify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.EventBus;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStartedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppingEvent;
import net.potatocloud.api.service.Service;
import net.potatocloud.plugins.shared.Config;
import net.potatocloud.plugins.shared.MessagesConfig;

import java.util.logging.Logger;

public class NotifyPlugin {

    private final ProxyServer server;
    private final CloudAPI cloudAPI = CloudAPI.getInstance();
    private final Logger logger;
    private final MessagesConfig messages;
    private final Config config;

    @Inject
    public NotifyPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        final String folder = "plugins/potatocloud-notify";

        config = new Config(folder, "config.yml");
        messages = new MessagesConfig(folder);
        config.load();
        messages.load();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        final EventBus eventBus = cloudAPI.getEventBus();

        if (config.yaml().getBoolean("messages.enable-service-starting")) {
            eventBus.subscribe(PreparedServiceStartingEvent.class, startingEvent -> sendMessage(startingEvent.serviceName(), "service-starting", false));
        }

        eventBus.subscribe(ServiceStartedEvent.class, startedEvent -> sendMessage(startedEvent.serviceName(), "service-started", true));

        if (config.yaml().getBoolean("messages.enable-service-stopping")) {
            eventBus.subscribe(ServiceStoppingEvent.class, stoppingEvent -> sendSimpleMessage("service-stopping", stoppingEvent.serviceName()));
        }

        eventBus.subscribe(ServiceStoppedEvent.class, stoppedEvent -> sendSimpleMessage("service-stopped", stoppedEvent.serviceName()));
    }

    private void sendMessage(String serviceName, String key, boolean clickEvent) {
        final Service service = cloudAPI.getServiceManager().getService(serviceName);

        Component message = messages.get(key)
                .replaceText(text -> text.match("%service%").replacement(service.getName()))
                .replaceText(text -> text.match("%port%").replacement(String.valueOf(service.getPort())))
                .replaceText(text -> text.match("%group%").replacement(service.getServiceGroup().getName()));

        if (clickEvent) {
            message = message.clickEvent(ClickEvent.runCommand("/server " + serviceName)).hoverEvent(HoverEvent.showText(
                    messages.get("hover-text").replaceText(text -> text.match("%service%").replacement(service.getName()))
            ));
        }

        final Component finalMessage = message;
        server.getAllPlayers().stream()
                .filter(player -> player.hasPermission(config.yaml().getString("permission")))
                .forEach(player -> player.sendMessage(finalMessage));
    }

    private void sendSimpleMessage(String key, String serviceName) {
        server.getAllPlayers().stream()
                .filter(player -> player.hasPermission(config.yaml().getString("permission")))
                .forEach(player -> player.sendMessage(messages.get(key).replaceText(text -> text.match("%service%").replacement(serviceName))));
    }
}
