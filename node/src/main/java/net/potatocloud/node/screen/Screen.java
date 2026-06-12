package net.potatocloud.node.screen;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class Screen {

    public static final String NODE_SCREEN = "node_screen";

    private final String name;
    private final List<String> cachedLogs;
    private final Set<Consumer<String>> logListeners;

    public Screen(String name) {
        this.name = name;
        this.cachedLogs = new CopyOnWriteArrayList<>();
        this.logListeners = new CopyOnWriteArraySet<>();
    }

    public String name() {
        return name;
    }

    public List<String> cachedLogs() {
        return Collections.unmodifiableList(cachedLogs);
    }

    public void addLog(String log) {
        synchronized (cachedLogs) {
            if (cachedLogs.size() >= 1000) {
                cachedLogs.removeFirst();
            }
            cachedLogs.add(log);
        }
        for (Consumer<String> listener : logListeners) {
            try {
                listener.accept(log);
            } catch (Exception ignored) {
            }
        }
    }

    public void subscribe(Consumer<String> listener) {
        logListeners.add(listener);
    }

    public void unsubscribe(Consumer<String> listener) {
        logListeners.remove(listener);
    }

}
