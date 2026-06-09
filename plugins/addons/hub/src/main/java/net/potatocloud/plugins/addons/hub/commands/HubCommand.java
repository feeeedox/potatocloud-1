package net.potatocloud.plugins.addons.hub.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.plugins.shared.MessagesConfig;

import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
public class HubCommand implements SimpleCommand {

    private final MessagesConfig messagesConfig;
    private final ProxyServer server;

    @Override
    public void execute(Invocation invocation) {
        final CommandSource source = invocation.source();

        if (!(source instanceof Player player)) {
            return;
        }

        final Optional<Service> service = CloudAPI.instance().playerManager().getCloudPlayer(player.getUniqueId()).service();
        if (service.isEmpty()) {
            return;
        }

        if (service.get().group().isFallback()) {
            player.sendMessage(messagesConfig.get("alreadyOnFallback"));
            return;
        }

        final Optional<RegisteredServer> fallback = getBestFallbackServer();
        if (fallback.isEmpty()) {
            player.sendMessage(messagesConfig.get("noFallbackFound"));
            return;
        }

        final RegisteredServer registeredServer = fallback.get();
        player.createConnectionRequest(registeredServer).fireAndForget();
        player.sendMessage(messagesConfig.get("connect")
                .replaceText(text -> text.match("%service%").replacement(registeredServer.getServerInfo().getName())));
    }

    private Optional<RegisteredServer> getBestFallbackServer() {
        return CloudAPI.instance().serviceManager().services().stream()
                .filter(service -> service.group().isFallback())
                .filter(service -> service.state() == ServiceState.RUNNING)
                .sorted(Comparator.comparingInt(Service::playerCount))
                .map(service -> server.getServer(service.name()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
