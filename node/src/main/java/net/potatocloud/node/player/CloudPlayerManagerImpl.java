package net.potatocloud.node.player;

import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.player.*;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.player.listeners.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final Set<CloudPlayer> onlinePlayers = new HashSet<>();
    private final NetworkServer server;
    private final ClusterManagerImpl clusterManager;

    public CloudPlayerManagerImpl(NetworkServer server, ClusterManagerImpl clusterManager) {
        this.server = server;
        this.clusterManager = clusterManager;

        server.on(CloudPlayerAddPacket.class, new CloudPlayerAddListener(this, server, clusterManager));
        server.on(CloudPlayerRemovePacket.class, new CloudPlayerRemoveListener(this, clusterManager));
        server.on(CloudPlayerUpdatePacket.class, new CloudPlayerUpdateListener(this, server, clusterManager));
        server.on(RequestCloudPlayersPacket.class, new RequestCloudPlayersListener(this));
        server.on(CloudPlayerConnectPacket.class, new CloudPlayerConnectListener(server));
    }

    public void registerPlayer(CloudPlayer player) {
        onlinePlayers.add(player);
    }

    public void unregisterPlayer(CloudPlayer player) {
        onlinePlayers.remove(player);
    }

    @Override
    public CloudPlayer getCloudPlayer(String username) {
        return onlinePlayers.stream()
                .filter(player -> player.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public CloudPlayer getCloudPlayer(UUID uniqueId) {
        return onlinePlayers.stream()
                .filter(player -> player.getUniqueId().equals(uniqueId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<CloudPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public void connectPlayerWithService(String playerName, String serviceName) {
        final CloudPlayerConnectPacket packet = new CloudPlayerConnectPacket(playerName, serviceName);
        final CloudPlayer player = getCloudPlayer(playerName);
        final Service proxy = player != null ? player.getConnectedProxy() : null;

        if (proxy != null && proxy.node().isPresent()) {
            final String nodeName = proxy.node().get().name();

            if (!clusterManager.isLocal(nodeName)) {
                clusterManager.sendTo(nodeName, packet);
                return;
            }
        }

        server.broadcast().connectors().send(packet);
    }

    @Override
    public void updatePlayer(CloudPlayer player) {
    }

}

