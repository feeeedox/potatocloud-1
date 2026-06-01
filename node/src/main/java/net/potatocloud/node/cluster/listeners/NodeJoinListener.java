package net.potatocloud.node.cluster.listeners;

import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.ConnectionType;
import net.potatocloud.network.NetworkConnection;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.cluster.NodeDiscoveryPacket;
import net.potatocloud.network.packet.packets.cluster.NodeJoinPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.cluster.ClusterNodeImpl;

import java.util.UUID;

public class NodeJoinListener implements PacketListener<NodeJoinPacket> {

    private final ClusterNode localNode;
    private final ClusterManagerImpl clusterManager;
    private final Logger logger;

    public NodeJoinListener(ClusterNode localNode, ClusterManagerImpl clusterManager, Logger logger) {
        this.localNode = localNode;
        this.clusterManager = clusterManager;
        this.logger = logger;
    }

    @Override
    public void handle(PacketContext<NodeJoinPacket> ctx) {
        final NodeJoinPacket packet = ctx.packet();
        final NetworkConnection connection = ctx.connection();
        final UUID nodeId = packet.nodeId();

        if (nodeId.equals(localNode.id())) {
            return;
        }

        connection.type(ConnectionType.NODE);

        final ClusterNodeImpl node = new ClusterNodeImpl(nodeId, packet.name(), packet.host(), packet.port(), packet.startedAt(), connection);
        clusterManager.add(node);

        if (clusterManager.isOutbound(connection)) {
            logger.info("Connected to cluster node &a" + node.name() + " &8(&a" + node.host() + "&8:&a" + node.port() + "&8)");
        } else {
            logger.info("Cluster node &a" + node.name() + " &7connected to the cluster &8(&a" + node.host() + "&8:&a" + node.port() + "&8)");
            connection.send(new NodeJoinPacket(localNode.id(), localNode.name(), localNode.host(), localNode.port(), localNode.startedAt()));

            connection.send(new NodeDiscoveryPacket(
                    clusterManager.remoteNodes().stream()
                            .filter(n -> !n.id().equals(nodeId))
                            .map(n -> (ClusterNode) n)
                            .toList()
            ));
        }
    }
}
