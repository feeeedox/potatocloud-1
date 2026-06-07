package net.potatocloud.webinterface.service.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.webinterface.WebInterfaceModule;
import net.potatocloud.webinterface.dto.event.WsEventDto;
import net.potatocloud.webinterface.dto.screen.ScreenLogDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class ScreenLogBroadcastService {

    private static final int MAX_LINES_PER_TICK = 50;
    private static final long SEND_TIMEOUT_MS = 500;

    private final Node node;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Integer> lastIndexBySession = new ConcurrentHashMap<>();
    private final Map<String, String> screenBySession = new ConcurrentHashMap<>();
    private final Map<String, Set<WsContext>> sessionsByScreen = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> sendIssuedAt = new ConcurrentHashMap<>();

    private final Map<String, Long> dbgSucceedCount = new ConcurrentHashMap<>();
    private final Map<String, Long> dbgFailCount = new ConcurrentHashMap<>();
    private final Map<String, Long> dbgTimeoutCount = new ConcurrentHashMap<>();
    private final Map<String, Long> dbgSkipCount = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;

    public void start() {
        if (scheduler != null && !scheduler.isShutdown()) return;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PotatoCloud-Screen-Broadcast");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void register(String screenName, WsContext context) {
        String sid = context.sessionId();
        sessionsByScreen.computeIfAbsent(screenName, k -> ConcurrentHashMap.newKeySet()).add(context);
        screenBySession.put(sid, screenName);
        sendIssuedAt.put(sid, new AtomicLong(0));
        dbgSucceedCount.put(sid, 0L);
        dbgFailCount.put(sid, 0L);
        dbgTimeoutCount.put(sid, 0L);
        dbgSkipCount.put(sid, 0L);

        Screen screen = node.getScreenManager().get(screenName);
        int tail = screen != null ? screen.cachedLogs().size() : 0;
        lastIndexBySession.put(sid, tail);

        log("REGISTER sid=" + sid + " screen=" + screenName + " tail=" + tail);
    }

    public void unregister(String screenName, WsContext context) {
        String sid = context.sessionId();

        log("UNREGISTER sid=" + sid
                + " succeeds=" + dbgSucceedCount.getOrDefault(sid, 0L)
                + " fails=" + dbgFailCount.getOrDefault(sid, 0L)
                + " timeouts=" + dbgTimeoutCount.getOrDefault(sid, 0L)
                + " skips=" + dbgSkipCount.getOrDefault(sid, 0L));

        Set<WsContext> sessions = sessionsByScreen.get(screenName);
        if (sessions != null) {
            sessions.remove(context);
            if (sessions.isEmpty()) sessionsByScreen.remove(screenName);
        }
        lastIndexBySession.remove(sid);
        screenBySession.remove(sid);
        sendIssuedAt.remove(sid);
        dbgSucceedCount.remove(sid);
        dbgFailCount.remove(sid);
        dbgTimeoutCount.remove(sid);
        dbgSkipCount.remove(sid);
    }

    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        sessionsByScreen.clear();
        lastIndexBySession.clear();
        screenBySession.clear();
        sendIssuedAt.clear();
    }


    private void tick() {
        sessionsByScreen.forEach((screenName, contexts) -> {
            Screen screen = node.getScreenManager().get(screenName);
            if (screen == null) return;

            Object[] logsSnapshot;
            try {
                logsSnapshot = screen.cachedLogs().toArray();
            } catch (Exception e) {
                return;
            }

            int total = logsSnapshot.length;

            for (WsContext context : contexts) {
                String sid = context.sessionId();

                if (!(context.session instanceof org.eclipse.jetty.websocket.api.Session jettySession) || !jettySession.isOpen()) {
                    continue;
                }

                int lastSent = lastIndexBySession.getOrDefault(sid, total);
                if (total <= lastSent) continue;

                int end = Math.min(lastSent + MAX_LINES_PER_TICK, total);

                List<ScreenLogDto> batch = new ArrayList<>(end - lastSent);
                for (int i = lastSent; i < end; i++) {
                    if (i >= 0 && i < logsSnapshot.length) {
                        Object logLine = logsSnapshot[i];
                        if (logLine != null) {
                            batch.add(new ScreenLogDto(screenName, logLine.toString()));
                        }
                    }
                }

                if (batch.isEmpty()) {
                    lastIndexBySession.put(sid, end);
                    continue;
                }

                WsEventDto<Object> event = WsEventDto.builder()
                        .type("screen:logs:batch")
                        .data(batch)
                        .build();

                try {
                    String json = objectMapper.writeValueAsString(event);

                    CompletableFuture<Void> sendFuture = new CompletableFuture<>();

                    jettySession.sendText(json, new org.eclipse.jetty.websocket.api.Callback() {
                        @Override
                        public void succeed() {
                            sendFuture.complete(null);
                        }

                        @Override
                        public void fail(Throwable x) {
                            sendFuture.completeExceptionally(x);
                        }
                    });

                    sendFuture.get(250, java.util.concurrent.TimeUnit.MILLISECONDS);

                    lastIndexBySession.put(sid, end);
                    dbgSucceedCount.merge(sid, 1L, Long::sum);

                } catch (java.util.concurrent.TimeoutException e) {
                    dbgFailCount.merge(sid, 1L, Long::sum);
                    log("JETTY_BACKPRESSURE_TIMEOUT sid=" + sid + " -> Pipeline verstopft. Schließe Session.");

                    try {
                        jettySession.disconnect();
                    } catch (Exception ignored) {
                    }

                } catch (Exception e) {
                    dbgFailCount.merge(sid, 1L, Long::sum);
                    log("SEND_EXCEPTION sid=" + sid + " -> " + e.getMessage());
                    try {
                        jettySession.disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private void log(String msg) {
        WebInterfaceModule.getInstance().getCloudAPI().getLogger()
                .info("[ScreenBroadcast] " + msg);
    }
}