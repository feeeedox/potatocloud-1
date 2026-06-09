package net.potatocloud.api.group;

import java.util.List;
import java.util.Optional;

public interface ServiceGroupManager {

    /**
     * Gets a service group by its name.
     *
     * @param name the name of the service group
     * @return the service group, or empty if not found
     */
    Optional<ServiceGroup> find(String name);

    /**
     * Gets the list of all service groups.
     *
     * @return the list of all service groups
     */
    List<ServiceGroup> groups();

    /**
     * Creates a builder for a new service group.
     *
     * @param name the name of the group
     * @return the builder
     */
    default ServiceGroupBuilder builder(String name) {
        return new ServiceGroupBuilder(name);
    }

    /**
     * Creates a new service group from the given object.
     *
     * @param group the service group to create
     */
    void create(ServiceGroup group);

    /**
     * Deletes the given service group.
     *
     * @param group the service group to delete
     */
    void delete(ServiceGroup group);

    /**
     * Updates an existing service group.
     *
     * @param group the service group to update
     */
    void update(ServiceGroup group);

    /**
     * Checks if a service group with the given name exists.
     *
     * @param name the name of the group
     * @return {@code true} if the group exists, otherwise {@code false}
     */
    default boolean exists(String name) {
        return find(name).isPresent();
    }
}
