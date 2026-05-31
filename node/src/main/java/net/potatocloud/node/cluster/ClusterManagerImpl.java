package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.ClusterManager;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.cluster.NodeStatus;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.NetworkConnection;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.netty.client.NettyNetworkClient;
import net.potatocloud.network.packet.PacketManager;
import net.potatocloud.network.packet.packets.cluster.NodeJoinPacket;
import net.potatocloud.network.packet.packets.cluster.NodeLeavePacket;
import net.potatocloud.node.cluster.listeners.NodeDisconnectListener;
import net.potatocloud.node.cluster.listeners.NodeJoinListener;
import net.potatocloud.node.cluster.listeners.NodeLeaveListener;
import net.potatocloud.node.config.ClusterConfig;

import java.util.*;

public class ClusterManagerImpl implements ClusterManager {

    private final ClusterNodeImpl localNode;

    private final ClusterConfig config;
    private final PacketManager packetManager;
    private final NetworkServer server;
    private final Logger logger;

    private final Map<UUID, ClusterNodeImpl> nodes = new HashMap<>();
    private final Set<NetworkConnection> outboundConnections = new HashSet<>();
    private final List<NettyNetworkClient> clients = new ArrayList<>();

    public ClusterManagerImpl(String localHost, int localPort, ClusterConfig config, PacketManager packetManager, NetworkServer server, Logger logger) {
        this.config = config;
        this.packetManager = packetManager;
        this.server = server;
        this.logger = logger;
        this.localNode = new ClusterNodeImpl(NodeId.load(), config.name(), localHost, localPort, NodeStatus.CONNECTED, null);
    }

    public void start() {
        server.on(NodeJoinPacket.class, new NodeJoinListener(localNode, this, logger));
        server.on(NodeLeavePacket.class, new NodeLeaveListener(this, logger));
        server.addDisconnectListener(new NodeDisconnectListener(this, logger));

        logger.info("Cluster enabled &8(&aid&8: &a" + localNode.id() + "&8, &aname&8: &a" + localNode.name() + "&8)");

        if (config.nodes() != null) {
            connectAll();
        }
    }

    private void connectAll() {
        for (String address : config.nodes()) {
            final String[] parts = address.split(":", 2);

            if (parts.length != 2) {
                logger.warn("Invalid cluster node address (expected host:port): " + address);
                continue;
            }

            try {
                connect(parts[0], Integer.parseInt(parts[1]));
            } catch (Exception e) {
                logger.warn("Failed to connect to cluster node " + address + " &8(&7" + e.getMessage() + "&8)");
            }
        }
    }

    private void connect(String host, int port) {
        final NettyNetworkClient client = new NettyNetworkClient(packetManager);

        client.addConnectionListener(() -> {
            final NetworkConnection connection = client.connection();

            outboundConnections.add(connection);
            connection.send(new NodeJoinPacket(localNode.id().toString(), localNode.name(), localNode.host(), localNode.port()));
        });

        client.connect(host, port);
        clients.add(client);
    }

    public void add(ClusterNodeImpl node) {
        nodes.put(node.id(), node);
    }

    public void remove(ClusterNodeImpl node) {
        nodes.remove(node.id());
        if (node.connection() != null) {
            outboundConnections.remove(node.connection());
        }
    }

    public void removeOutbound(NetworkConnection connection) {
        outboundConnections.remove(connection);
    }

    // returns true if we opened this connection
    public boolean isOutbound(NetworkConnection connection) {
        return outboundConnections.contains(connection);
    }

    public void close() {
        final NodeLeavePacket leavePacket = new NodeLeavePacket(localNode.id().toString());

        nodes.values().stream()
                .filter(node -> node.status() == NodeStatus.CONNECTED && node.connection() != null)
                .forEach(node -> node.connection().send(leavePacket));

        clients.forEach(NettyNetworkClient::close);
    }

    @Override
    public ClusterNode localNode() {
        return localNode;
    }

    @Override
    public Collection<ClusterNode> nodes() {
        final List<ClusterNode> all = new ArrayList<>();
        nodes.values().stream()
                .filter(node -> node.status() == NodeStatus.CONNECTED)
                .forEach(all::add);

        all.add(localNode);
        return Collections.unmodifiableList(all);
    }

    @Override
    public Optional<ClusterNode> get(UUID nodeId) {
        if (localNode.id().equals(nodeId)) {
            return Optional.of(localNode);
        }
        return Optional.ofNullable(nodes.get(nodeId));
    }

    public Optional<ClusterNodeImpl> getNode(UUID nodeId) {
        return Optional.ofNullable(nodes.get(nodeId));
    }

    public Optional<ClusterNodeImpl> getByConnection(NetworkConnection connection) {
        return nodes.values().stream()
                .filter(node -> connection.equals(node.connection()))
                .findFirst();
    }
}
