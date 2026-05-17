package net.potatocloud.node.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerAddPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.player.CloudPlayerManagerImpl;

@RequiredArgsConstructor
public class CloudPlayerAddListener implements PacketListener<CloudPlayerAddPacket> {

    private final CloudPlayerManagerImpl playerManager;
    private final NetworkServer server;

    @Override
    public void handle(PacketContext<CloudPlayerAddPacket> ctx) {
        final CloudPlayerAddPacket packet = ctx.packet();
        final CloudPlayer player = new CloudPlayerImpl(packet.username(), packet.uniqueId(), packet.connectedProxyName());

        playerManager.registerPlayer(player);

        final Node node = Node.getInstance();

        server.generateBroadcast().exclude(ctx.connection()).broadcast(packet);

        final NodeConfig config = node.getConfig();
        if (config.console().logPlayerConnections()) {
            node.getLogger().info("Player &a" + player.getUsername() + " &7connected to the network &8[&7UUID&8: &a"
                    + player.getUniqueId() + "&8, &7Proxy&8: &a" + player.getConnectedProxyName() + "&8]");
        }
    }
}
