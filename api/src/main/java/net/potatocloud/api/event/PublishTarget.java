package net.potatocloud.api.event;

/**
 * Defines where an event should be published.
 */
public enum PublishTarget {

    /**
     * The event will only be published locally.
     */
    LOCAL,

    /**
     * The event will only be published over the network.
     */
    NETWORK,

    /**
     * The event will be published both locally and over the network.
     */
    BOTH

}