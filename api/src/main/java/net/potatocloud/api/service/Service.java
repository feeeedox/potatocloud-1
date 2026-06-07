package net.potatocloud.api.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.PropertyHolder;
import net.potatocloud.api.utils.TimeFormatter;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface Service extends PropertyHolder {

    /**
     * Gets the name of the service.
     *
     * @return the name of the service
     */
    String getName();

    /**
     * Gets the name of the cluster node this service's group is assigned to.
     *
     * @return the node name, or {@code null} if the group is not assigned to a specific node
     */
    default String nodeName() {
        final ServiceGroup group = getServiceGroup();
        return group != null ? group.nodeName() : null;
    }

    /**
     * Gets the cluster node this service's group is assigned to.
     *
     * @return the cluster node, or an empty optional if not assigned
     */
    default Optional<ClusterNode> node() {
        final ServiceGroup group = getServiceGroup();
        return group != null ? group.node() : Optional.empty();
    }

    /**
     * Gets the id of the service.
     *
     * @return the id of the service
     */
    int getServiceId();

    /**
     * Gets whether the service is online or not.
     *
     * @return {@code true} if the service is online, otherwise {@code false}
     */
    default boolean isOnline() {
        return getStatus() == ServiceStatus.RUNNING;
    }

    /**
     * Gets the status of the service.
     *
     * @return the status of the service
     */
    ServiceStatus getStatus();

    /**
     * Sets the status of the service.
     *
     * @param status the new status of the service
     */
    void setStatus(ServiceStatus status);

    /**
     * Gets the timestamp of the service start.
     *
     * @return the timestamp of the service start
     */
    long getStartTimestamp();

    /**
     * Gets the formatted timestamp of the service start.
     *
     * @return the formatted timestamp of the service start
     */
    default String getFormattedStartTimestamp() {
        return TimeFormatter.formatAsDateAndTime(getStartTimestamp());
    }

    /**
     * Gets the uptime of the service.
     *
     * @return the uptime of the service
     */
    default long getUptime() {
        return System.currentTimeMillis() - getStartTimestamp();
    }

    /**
     * Gets the formatted uptime of the service.
     *
     * @return the formatted uptime of the service
     */
    default String getFormattedUptime() {
        return TimeFormatter.formatAsDuration(getUptime());
    }

    /**
     * Gets the online players of the service.
     *
     * @return the online players of the service
     */
    default Set<CloudPlayer> getOnlinePlayers() {
        return CloudAPI.instance().playerManager().getOnlinePlayers().stream()
                .filter(player -> getName().equals(player.getConnectedServiceName()))
                .collect(Collectors.toSet());
    }

    /**
     * Gets the online player count of the service.
     *
     * @return the online player count of the service
     */
    default int getOnlinePlayerCount() {
        return getOnlinePlayers().size();
    }

    /**
     * Gets whether the service is full or not.
     *
     * @return {@code true} if the service is full, otherwise {@code false}
     */
    default boolean isFull() {
        return getOnlinePlayerCount() >= getMaxPlayers();
    }

    /**
     * Gets the maximum players of the service.
     *
     * @return the maximum players of the service
     */
    int getMaxPlayers();

    /**
     * Sets the maximum players of the service.
     *
     * @param maxPlayers the new maximum players of the service
     */
    void setMaxPlayers(int maxPlayers);


    /**
     * Gets the used memory of the service.
     *
     * @return the used memory of the service
     */
    int getUsedMemory();

    /**
     * Gets the host of the node this service runs on.
     *
     * @return the host address of the node
     */
    String host();

    /**
     * Gets the port of the service.
     *
     * @return the port of the service
     */
    int getPort();

    /**
     * Gets the group of the service.
     *
     * @return the group of the service
     */
    ServiceGroup getServiceGroup();

    /**
     * Shuts down the service.
     *
     * @return a CompletableFuture that completes when the shutdown is done
     */
    default CompletableFuture<Void> shutdown() {
        return CloudAPI.instance().serviceManager().stopService(this);
    }

    /**
     * Executes a command on the service.
     *
     * @param command the command to execute
     * @return {@code true} if the command was executed successfully, otherwise {@code false}
     */
    default boolean executeCommand(String command) {
        return CloudAPI.instance().serviceManager().executeCommand(this, command);
    }

    /**
     * Copies service files to a template.
     *
     * @param template the template to copy to
     * @param filter   the filter to apply
     */
    default void copy(String template, String filter) {
        CloudAPI.instance().serviceManager().copy(this, template, filter);
    }

    /**
     * Copies service files to a template.
     *
     * @param template the template to copy to
     */
    default void copy(String template) {
        CloudAPI.instance().serviceManager().copy(this, template);
    }

    /**
     * Updates the service.
     */
    default void update() {
        CloudAPI.instance().serviceManager().updateService(this);
    }
}
