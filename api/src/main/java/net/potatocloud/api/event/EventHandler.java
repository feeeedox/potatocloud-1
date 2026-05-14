package net.potatocloud.api.event;

public interface EventHandler<T extends Event> {

    /**
     * Called then the event was fired
     *
     * @param event the event
     */
    void handle(T event);

}
