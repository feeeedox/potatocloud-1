package net.potatocloud.webinterface.service.broadcast;

import io.javalin.websocket.WsContext;
import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.screen.Screen;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ScreenLogBroadcastService {

    private final Node node;

    private final Map<String, Set<WsContext>> sessionsByScreen = new ConcurrentHashMap<>();
    private final Map<String, Integer> lastIndexByScreen = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;

    public void start() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "PotatoCloud-Screen-Broadcast");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void register(String screenName, WsContext context) {
        sessionsByScreen.computeIfAbsent(screenName, key -> ConcurrentHashMap.newKeySet()).add(context);

        Screen screen = node.getScreenManager().get(screenName);
        if (screen != null) {
            lastIndexByScreen.putIfAbsent(screenName, screen.cachedLogs().size());
        }
    }

    public void unregister(String screenName, WsContext context) {
        Set<WsContext> sessions = sessionsByScreen.get(screenName);
        if (sessions == null) {
            return;
        }

        sessions.remove(context);
        if (sessions.isEmpty()) {
            sessionsByScreen.remove(screenName);
            lastIndexByScreen.remove(screenName);
        }
    }

    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        sessionsByScreen.clear();
        lastIndexByScreen.clear();
    }

    private void tick() {
        sessionsByScreen.forEach((screenName, contexts) -> {
            Screen screen = node.getScreenManager().get(screenName);
            if (screen == null) {
                return;
            }

            var logs = screen.cachedLogs();
            int total = logs.size();
            int lastSent = lastIndexByScreen.getOrDefault(screenName, 0);

            if (total <= lastSent) {
                return;
            }

            for (int i = lastSent; i < total; i++) {
                String line = logs.get(i);
                for (WsContext context : contexts) {
                    if (context.session.isOpen()) {
                        try {
                            context.send(line);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            lastIndexByScreen.put(screenName, total);
        });
    }
}

