package net.potatocloud.node.cluster;

import net.potatocloud.api.cluster.ClusterManager;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.network.NetworkConnection;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.netty.client.NettyNetworkClient;
import net.potatocloud.network.packet.Packet;
import net.potatocloud.network.packet.PacketManager;
import net.potatocloud.network.packet.packets.cluster.ClusterSyncPacket;
import net.potatocloud.network.packet.packets.cluster.HeartbeatPacket;
import net.potatocloud.network.packet.packets.cluster.NodeDiscoveryPacket;
import net.potatocloud.network.packet.packets.cluster.NodeJoinPacket;
import net.potatocloud.network.packet.packets.cluster.NodeLeavePacket;
import net.potatocloud.node.cluster.listeners.ClusterSyncListener;
import net.potatocloud.node.cluster.listeners.HeartbeatListener;
import net.potatocloud.node.cluster.listeners.NodeDiscoveryListener;
import net.potatocloud.node.cluster.listeners.NodeDisconnectListener;
import net.potatocloud.node.cluster.listeners.NodeJoinListener;
import net.potatocloud.node.cluster.listeners.NodeLeaveListener;
import net.potatocloud.node.config.ClusterConfig;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.player.CloudPlayerManagerImpl;
import net.potatocloud.node.service.ServiceManagerImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterManagerImpl implements ClusterManager {

    private final ClusterNodeImpl localNode;

    private final ClusterConfig config;
    private final PacketManager packetManager;
    private final NetworkServer server;
    private final Logger logger;

    private final Map<String, ClusterNodeImpl> nodes = new ConcurrentHashMap<>();
    private final Set<NetworkConnection> outboundConnections = ConcurrentHashMap.newKeySet();
    private final List<NettyNetworkClient> clients = new ArrayList<>();
    private final Set<String> connectingAddresses = ConcurrentHashMap.newKeySet();

    private HeartbeatScheduler heartbeatScheduler;

    public ClusterManagerImpl(String localHost, int localPort, ClusterConfig config, PacketManager packetManager, NetworkServer server, Logger logger) {
        this.config = config;
        this.packetManager = packetManager;
        this.server = server;
        this.logger = logger;
        this.localNode = new ClusterNodeImpl(config.name(), localHost, localPort, System.currentTimeMillis(), null);
    }

    public void start(ServiceGroupManagerImpl groupManager, ServiceManagerImpl serviceManager, CloudPlayerManagerImpl playerManager) {
        server.on(NodeJoinPacket.class, new NodeJoinListener(localNode, this, logger, groupManager, serviceManager, playerManager));
        server.on(NodeLeavePacket.class, new NodeLeaveListener(this, logger));
        server.on(HeartbeatPacket.class, new HeartbeatListener(this));
        server.on(NodeDiscoveryPacket.class, new NodeDiscoveryListener(this));
        server.on(ClusterSyncPacket.class, new ClusterSyncListener(groupManager, serviceManager, playerManager));
        server.addDisconnectListener(new NodeDisconnectListener(this, logger));

        heartbeatScheduler = new HeartbeatScheduler(this, localNode, logger);
        heartbeatScheduler.start();

        logger.info("Cluster enabled &8(&aname&8: &a" + localNode.name() + "&8)");

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

            connect(parts[0], Integer.parseInt(parts[1]));
        }
    }

    public void connect(String host, int port) {
        if (!connectingAddresses.add(host + ":" + port)) {
            return;
        }

        final NettyNetworkClient client = new NettyNetworkClient(packetManager);

        client.addConnectionListener(() -> {
            final NetworkConnection connection = client.connection();
            outboundConnections.add(connection);
            connection.send(new NodeJoinPacket(localNode.name(), localNode.host(), localNode.port(), localNode.startedAt()));
        });

        try {
            client.connect(host, port);
            clients.add(client);
        } catch (Exception e) {
            logger.warn("Failed to connect to cluster node " + host + ":" + port + " &8(&7" + e.getMessage() + "&8)");
        }
    }

    public void add(ClusterNodeImpl node) {
        nodes.put(node.name(), node);
    }

    public void remove(ClusterNodeImpl node) {
        nodes.remove(node.name());
        if (node.connection() != null) {
            outboundConnections.remove(node.connection());
        }
    }

    // returns true if we opened this connection
    public boolean isOutbound(NetworkConnection connection) {
        return outboundConnections.contains(connection);
    }

    public void sendTo(String nodeName, Packet packet) {
        final ClusterNodeImpl node = nodes.get(nodeName);
        if (node == null || node.connection() == null) {
            return;
        }
        node.connection().send(packet);
    }

    public void broadcast(Packet packet) {
        nodes.values().stream()
                .filter(node -> node.connection() != null)
                .forEach(node -> node.connection().send(packet));
    }

    public void close() {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.stop();
        }

        broadcast(new NodeLeavePacket(localNode.name()));
        clients.forEach(NettyNetworkClient::close);
    }

    @Override
    public ClusterNode localNode() {
        return localNode;
    }

    @Override
    public Collection<ClusterNode> nodes() {
        final List<ClusterNode> all = new ArrayList<>(nodes.values());
        all.add(localNode);
        return Collections.unmodifiableList(all);
    }

    public Collection<ClusterNodeImpl> remoteNodes() {
        return nodes.values();
    }

    @Override
    public Optional<ClusterNode> get(String name) {
        if (localNode.name().equals(name)) {
            return Optional.of(localNode);
        }
        return Optional.ofNullable(nodes.get(name));
    }

    public Optional<ClusterNodeImpl> remoteNode(String nodeName) {
        return Optional.ofNullable(nodes.get(nodeName));
    }

    public Optional<ClusterNodeImpl> remoteNode(NetworkConnection connection) {
        return nodes.values().stream()
                .filter(node -> connection.equals(node.connection()))
                .findFirst();
    }
}
