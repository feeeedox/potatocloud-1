package net.potatocloud.api.player;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.property.PropertyHolder;
import net.potatocloud.api.service.Service;

import java.util.Optional;
import java.util.UUID;

public interface CloudPlayer extends PropertyHolder {

    /**
     * Gets the username of the player.
     *
     * @return the username of the player
     */
    String getUsername();

    /**
     * Gets the name of the cluster node this player's proxy is running on.
     *
     * @return the node name, or {@code null} if not found
     */
    default String nodeName() {
        final Service service = getConnectedService();
        return service != null ? service.nodeName() : null;
    }

    /**
     * Gets the cluster node this player's proxy is running on.
     *
     * @return the cluster node, or an empty optional if not found
     */
    default Optional<ClusterNode> node() {
        final Service service = getConnectedService();
        return service != null ? service.node() : Optional.empty();
    }

    /**
     * Gets the unique id of the player.
     *
     * @return the unique id of the player
     */
    UUID getUniqueId();

    /**
     * Gets the connected proxy name of the player.
     *
     * @return the connected proxy name of the player
     */
    String getConnectedProxyName();

    /**
     * Gets the connected service name of the player.
     *
     * @return the connected service name of the player
     */
    String getConnectedServiceName();

    /**
     * Gets the connected proxy of the player.
     *
     * @return the connected proxy of the player
     */
    default Service getConnectedProxy() {
        return CloudAPI.instance().serviceManager().getService(getConnectedProxyName());
    }

    /**
     * Gets the connected service of the player.
     *
     * @return the connected service of the player
     */
    default Service getConnectedService() {
        return CloudAPI.instance().serviceManager().getService(getConnectedServiceName());
    }

    /**
     * Connects the player with a service.
     *
     * @param service the service to connect with
     */
    default void connectWithService(Service service) {
        CloudAPI.instance().playerManager().connectPlayerWithService(this, service);
    }

    /**
     * Connects the player with a service.
     *
     * @param serviceName the service name to connect with
     */
    default void connectWithService(String serviceName) {
        connectWithService(CloudAPI.instance().serviceManager().getService(serviceName));
    }

    /**
     * Updates the player.
     */
    default void update() {
        CloudAPI.instance().playerManager().updatePlayer(this);
    }
}
