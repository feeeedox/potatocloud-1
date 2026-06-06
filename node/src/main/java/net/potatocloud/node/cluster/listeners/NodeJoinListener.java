package net.potatocloud.node.cluster.listeners;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkConnection;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.ClusterSyncPacket;
import net.potatocloud.network.packet.packets.cluster.NodeDiscoveryPacket;
import net.potatocloud.network.packet.packets.cluster.NodeJoinPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.cluster.ClusterNodeImpl;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.player.CloudPlayerManagerImpl;
import net.potatocloud.node.service.ServiceManagerImpl;

public class NodeJoinListener implements PacketListener<NodeJoinPacket> {

    private final ClusterNode localNode;
    private final ClusterManagerImpl clusterManager;
    private final Logger logger;
    private final ServiceGroupManagerImpl groupManager;
    private final ServiceManagerImpl serviceManager;
    private final CloudPlayerManagerImpl playerManager;

    public NodeJoinListener(ClusterNode localNode, ClusterManagerImpl clusterManager, Logger logger, ServiceGroupManagerImpl groupManager, ServiceManagerImpl serviceManager, CloudPlayerManagerImpl playerManager) {
        this.localNode = localNode;
        this.clusterManager = clusterManager;
        this.logger = logger;
        this.groupManager = groupManager;
        this.serviceManager = serviceManager;
        this.playerManager = playerManager;
    }

    @Override
    public void handle(PacketContext<NodeJoinPacket> ctx) {
        final NodeJoinPacket packet = ctx.packet();
        final NetworkConnection connection = ctx.connection();
        final String nodeName = packet.nodeName();

        if (nodeName.equals(localNode.name())) {
            return;
        }

        final String localVersion = CloudAPI.VERSION.toString();
        if (!localVersion.equals(packet.nodeVersion())) {
            logger.warn("Cluster node &a" + nodeName + " &7join rejected &8(&7version mismatch: remote=&a" + packet.nodeVersion() + " &7local=&a" + localVersion + "&8)");
            connection.close();
            return;
        }

        if (clusterManager.remoteNode(nodeName).isPresent()) {
            logger.warn("Cluster node &a" + nodeName + " &7join rejected &8(&7duplicate name&8)");
            connection.close();
            return;
        }

        connection.type(ConnectionType.NODE);

        final ClusterNodeImpl node = new ClusterNodeImpl(nodeName, packet.host(), packet.port(), packet.startedAt(), connection);
        clusterManager.add(node);

        if (clusterManager.isOutbound(connection)) {
            logger.info("Connected to cluster node &a" + node.name() + " &8(&a" + node.host() + "&8:&a" + node.port() + "&8)");
        } else {
            logger.info("Cluster node &a" + node.name() + " &7connected to the cluster &8(&a" + node.host() + "&8:&a" + node.port() + "&8)");
            connection.send(new NodeJoinPacket(localNode.name(), localNode.host(), localNode.port(), localNode.startedAt(), CloudAPI.VERSION.toString()));

            connection.send(new NodeDiscoveryPacket(
                    clusterManager.remoteNodes().stream()
                            .filter(n -> !n.name().equals(nodeName))
                            .map(n -> (ClusterNode) n)
                            .toList()
            ));
        }

        final long syncStart = System.currentTimeMillis();

        connection.send(new ClusterSyncPacket(
                groupManager.getAllServiceGroups(),
                serviceManager.getAllServices(),
                playerManager.getOnlinePlayers()
        ));

        logger.debug("Cluster sync sent to node &a" + node.name() + " &7in &a" + (System.currentTimeMillis() - syncStart) + "ms");
    }
}
