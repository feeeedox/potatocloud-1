package net.potatocloud.api.event;

public interface EventHandler<T extends Event> {

    /**
     * Called when the event was fired
     *
     * @param event the event
     */
    void handle(T event);

}
