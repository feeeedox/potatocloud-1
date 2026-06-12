package net.potatocloud.api.player;

import net.potatocloud.api.service.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CloudPlayerManager {

    /**
     * Gets a player by its unique id.
     *
     * @param uniqueId the unique id of the player
     * @return the player
     */
    Optional<CloudPlayer> find(UUID uniqueId);

    /**
     * Gets a player by its username.
     *
     * @param username the name of the player
     * @return the player
     */
    Optional<CloudPlayer> find(String username);

    /**
     * Gets the set of all online players.
     *
     * @return the set of all online players
     */
    Set<CloudPlayer> players();

    /**
     * Connects the player to the given service.
     *
     * @param player  the player to connect
     * @param service the service to connect with
     */
    void connectTo(CloudPlayer player, Service service);

    /**
     * Updates an existing player.
     *
     * @param player the player to update
     */
    void update(CloudPlayer player);

}
