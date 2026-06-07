package net.potatocloud.node.player.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerAddPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.player.CloudPlayerManagerImpl;

@RequiredArgsConstructor
public class CloudPlayerAddListener implements PacketListener<CloudPlayerAddPacket> {

    private final CloudPlayerManagerImpl playerManager;
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<CloudPlayerAddPacket> ctx) {
        final CloudPlayerAddPacket packet = ctx.packet();

        playerManager.registerPlayer(packet.player());

        final Node node = Node.getInstance();

        server.broadcast().connectors().exclude(ctx.connection()).send(packet);

        if (ctx.connection().type() == ConnectionType.CONNECTOR) {
            clusterManager.broadcast(packet);
        }

        final NodeConfig config = node.getConfig();
        if (config.console().logPlayerConnections()) {
            node.getLogger().info("Player &a" + packet.player().getUsername() + " &7connected to the network &8[&7UUID&8: &a"
                    + packet.player().getUniqueId() + "&8, &7Proxy&8: &a" + packet.player().getConnectedProxyName() + "&8]");
        }
    }
}
