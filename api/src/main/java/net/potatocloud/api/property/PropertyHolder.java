package net.potatocloud.api.property;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.events.property.PropertyChangedEvent;

import java.util.List;
import java.util.Map;

public interface PropertyHolder {

    /**
     * Gets the map of properties by name.
     *
     * @return the property map
     */
    Map<String, Property<?>> propertyMap();

    /**
     * Gets the list of all properties of this holder.
     *
     * @return the list of all properties
     */
    default List<Property<?>> properties() {
        return propertyMap().values().stream().toList();
    }

    /**
     * Gets the name of this property holder.
     *
     * @return the name of this property holder
     */
    String name();

    /**
     * Gets a property object by name.
     *
     * @param name the name of the property
     * @param <T>  the type of the property value
     * @return the property, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    default <T> Property<T> property(String name) {
        return (Property<T>) propertyMap().get(name);
    }

    /**
     * Gets a property object by reference.
     *
     * @param key the property key
     * @param <T> the type of the property value
     * @return the property, or {@code null} if not found
     */
    default <T> Property<T> property(Property<T> key) {
        return property(key.name());
    }

    /**
     * Gets the current value of a property directly.
     * Returns the property's default value if not set.
     *
     * @param key the property key
     * @param <T> the type of the property value
     * @return the current value, or the default value if not set
     */
    default <T> T get(Property<T> key) {
        final Property<T> existing = property(key.name());
        return existing != null ? existing.value() : key.defaultValue();
    }

    /**
     * Sets a property value and optionally fires {@link PropertyChangedEvent}.
     *
     * @param key       the property key
     * @param value     the new value
     * @param fireEvent {@code true} to fire a PropertyChangedEvent
     * @param <T>       the type of the property value
     */
    default <T> void set(Property<T> key, T value, boolean fireEvent) {
        Property<T> existing = property(key.name());
        Object oldValue = null;

        if (existing != null) {
            oldValue = existing.value();
            if (oldValue.equals(value)) {
                return;
            }
            existing.value(value);
            key = existing;
        } else {
            key.value(value);
            propertyMap().put(key.name(), key);
        }

        if (fireEvent) {
            CloudAPI.instance().eventBus().publish(
                    new PropertyChangedEvent(name(), key.name(), oldValue, value)
            );
        }
    }

    /**
     * Sets a property value and fires {@link PropertyChangedEvent}.
     *
     * @param key   the property key
     * @param value the new value
     * @param <T>   the type of the property value
     */
    default <T> void set(Property<T> key, T value) {
        set(key, value, true);
    }

    /**
     * Stores a property using its own current value.
     *
     * @param key the property to store
     * @param <T> the type of the property value
     */
    default <T> void set(Property<T> key) {
        set(key, key.value());
    }

    /**
     * Checks whether a property with the given name exists.
     *
     * @param name the name of the property
     * @return {@code true} if a property with the given name exists
     */
    default boolean hasProperty(String name) {
        return propertyMap().containsKey(name);
    }

    /**
     * Checks whether the given property exists.
     *
     * @param key the property key
     * @return {@code true} if the property exists
     */
    default boolean hasProperty(Property<?> key) {
        return propertyMap().containsKey(key.name());
    }
}
