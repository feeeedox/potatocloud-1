package net.potatocloud.connector.cluster;

import net.potatocloud.api.cluster.ClusterManager;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.network.NetworkClient;
import net.potatocloud.network.packet.packets.cluster.ClusterNodeAddPacket;
import net.potatocloud.network.packet.packets.cluster.ClusterNodeRemovePacket;
import net.potatocloud.network.packet.packets.cluster.ClusterNodesResponsePacket;
import net.potatocloud.network.packet.packets.cluster.RequestClusterNodesPacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterManagerImpl implements ClusterManager {

    private volatile ClusterNode localNode;
    private final Map<String, ClusterNode> nodes = new ConcurrentHashMap<>();

    public ClusterManagerImpl(NetworkClient client) {
        client.on(ClusterNodesResponsePacket.class, ctx -> {
            localNode = ctx.packet().localNode();
            ctx.packet().remoteNodes().forEach(node -> nodes.put(node.name(), node));
        });

        client.on(ClusterNodeAddPacket.class, ctx -> nodes.put(ctx.packet().node().name(), ctx.packet().node()));

        client.on(ClusterNodeRemovePacket.class, ctx -> nodes.remove(ctx.packet().nodeName()));

        client.send(new RequestClusterNodesPacket());
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

    @Override
    public Optional<ClusterNode> get(String name) {
        if (localNode != null && localNode.name().equals(name)) {
            return Optional.of(localNode);
        }
        return Optional.ofNullable(nodes.get(name));
    }
}
